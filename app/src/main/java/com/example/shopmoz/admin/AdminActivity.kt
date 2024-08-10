package com.example.productsadder

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.shopmoz.R
import com.example.shopmoz.databinding.ActivityAdminBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class AdminActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAdminBinding.inflate(layoutInflater) }
    private var selectedImages = mutableListOf<Uri>()
    private var selectedColors= mutableListOf<Int>()
    private val productsStorage= Firebase.storage.reference
    private val firestore= Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle("Cor do Produto")
                .setPositiveButton("Selecionar", object : ColorEnvelopeListener{
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColors.add(it.color)
                            updateColor()
                        }
                    }
                })
                .setNegativeButton("Cancelar"){ colorPicker,_->
                    colorPicker.dismiss()

                }.show()
        }

        //Para abrir varias imagens
        val selectImagesActivityResult= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if (result.resultCode== RESULT_OK){
                val intent= result.data

                //PARA SELECIONAR VARIAS IMAGENS DUMA SO VEZ
                if (intent?.clipData !=null){
                    val count = intent.clipData?.itemCount?:0
                    (0 until count).forEach {
                        val imageUri= intent.clipData?.getItemAt(it)?.uri
                        imageUri?.let {
                            selectedImages.add(it)
                        }
                    }
                }else{
                    /// PARA SELECIONAR UMA IMAGEM

                    val imageUri= intent?.data
                    imageUri?.let {
                        selectedImages.add(it)
                    }
                }
                updateImages()
            }

        }

        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type= "image/*"

            selectImagesActivityResult.launch(intent)
        }

        binding.buttonSaveProduct.setOnClickListener {
            val productValidation = validateInformation()
            if (!productValidation){
                Toast.makeText(this,"Preencha Todos os Campos ", Toast.LENGTH_LONG).show()
            }
            saveProduct()
        }


    }


    private fun updateImages(){
        binding.tvSelectedImages.text= selectedImages.size.toString()
    }
    private fun updateColor(){
        var colors= ""
        selectedColors.forEach{
            colors= "$colors ${Integer.toHexString(it) }"
        }
        binding.tvSelectedColors.text=colors

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId== R.id.saveProduct){
            val productValidation = validateInformation()
            if (!productValidation){
                Toast.makeText(this,"Preencha Todos os Campos ", Toast.LENGTH_LONG).show()
                return false
            }
            saveProduct()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveProduct() {
        val name = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.offerPercentage.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val sizes = getSizeList(binding.edSizes.text.toString().trim())
        val imagesByteArrays = getImagesByteArays()
        val images = mutableListOf<String>()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    showLoading()
                }
                async {
                    imagesByteArrays.forEach { byteArray ->
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imageStorage = productsStorage.child("products/images/$id")
                            val result = imageStorage.putBytes(byteArray).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }


                }.await()


            } catch (e: Exception) {
                hideLoading()
                Log.e("Error", e.message.toString())
            }

            val product = Product(
                UUID.randomUUID().toString(),
                name,
                category,
                price.toFloat(),
                if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                if (description.isEmpty()) null else description,
                if (selectedColors.isEmpty()) null else selectedColors,
                sizes,
                images
            )

            firestore.collection("Products").add(product).addOnSuccessListener {
                hideLoading()

            }.addOnFailureListener { e ->
                hideLoading()
                Log.e("Error", e.message.toString())
            }
        }
    }

    private fun hideLoading(){

        binding.progressBar.visibility= View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility= View.VISIBLE
    }

    private fun getImagesByteArays(): List<ByteArray> {

        val imagesByteArray= mutableListOf<ByteArray>()
        selectedImages.forEach {
            val stream= ByteArrayOutputStream()
            val imageBmp= MediaStore.Images.Media.getBitmap(contentResolver, it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG,100,stream)){
                imagesByteArray.add(stream.toByteArray())
            }

        }
        return imagesByteArray
    }

    //usamos , para separar na nossa lista
    private fun getSizeList(sizeStr: String): List<String>? {
        if (sizeStr.trim().isEmpty()) return null
        return sizeStr.trim().split(",").map { it.trim() }
    }

    private fun validateInformation(): Boolean {
        if (binding.edPrice.text.toString().trim().isEmpty()) return false
        if (binding.edName.text.toString().trim().isEmpty()) return false
        if (binding.edCategory.text.toString().trim().isEmpty()) return false
        if (selectedImages.isEmpty()) return false
        return true
    }


}