package com.rrstudio.notasfast2.ui.editNote

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.rrstudio.notasfast2.MainActivity
import com.rrstudio.notasfast2.R
import com.rrstudio.notasfast2.data.local.NoteAplication
import com.rrstudio.notasfast2.data.model.NoteEntity
import com.rrstudio.notasfast2.databinding.FragmentEditDateBinding
import com.rrstudio.notasfast2.databinding.FragmentEditNoteBinding
import com.rrstudio.notasfast2.ui.editDate.EditDateFragment
import com.rrstudio.notasfast2.ui.note.NoteFragment
import com.rrstudio.notasfast2.ui.note.NoteFragmentDirections
import com.rrstudio.notasfast2.ui.noteDetail.NoteDetailFragmentArgs
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class EditNoteFragment : Fragment(R.layout.fragment_edit_note) {

    private lateinit var mBinding:FragmentEditNoteBinding
    private var mNoteFragment: NoteFragment? = null
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private var mNoteEntity: NoteEntity? = null
    private val args by navArgs<EditNoteFragmentArgs>()


    private var mPhotoSelectedUri: Uri? = null
    private var mPhotoSelectedBitmap: Bitmap? = null

    private var mPhotoSelectedString: String? = null

    private var mData: ByteArray? = null

    private val RC_GALLERY = 18
    private val RC_CAMERA = 1



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentEditNoteBinding.inflate(inflater, container, false)

        mBinding.etDate.setOnClickListener { showDatePickerDialog() }
        mBinding.btnSelect.setOnClickListener {dispatchTakePictureIntent() }


        return mBinding.root
    }


    private fun showDatePickerDialog() {
        val datePicker = EditDateFragment { day, month, year -> onDateSelected(day, month, year) }

        mActivity?.let { datePicker.show(it.supportFragmentManager, "datePicker") }
    }



    private fun onDateSelected(day: Int, month: Int, year: Int) {
        mBinding.etDate.setText("$day/${month+1}/$year")

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = args.id


        if (id != 0L){
            mIsEditMode = true
            getNote(id)
        }else{
            mIsEditMode = false
            mNoteEntity = NoteEntity(name = "", date = "", description = "", image = "")
        }

          setupActionBar()
        mBinding.etDate.addTextChangedListener { validateFields(mBinding.tilDate)  }
        mBinding.etName.addTextChangedListener { validateFields(mBinding.tilName) }
    }






    private fun getNote(id: Long) {
        doAsync {
            mNoteEntity = NoteAplication.database.noteDao().getNoteById(id)
            uiThread {
                if (mNoteEntity != null) setUiNote(mNoteEntity!!)
            }
        }
    }

    private fun setUiNote(noteEntity: NoteEntity) {
        with (mBinding){
            etName.text = noteEntity.name.editable()
            etDate.text = noteEntity.date.editable()
            etDescription.text = noteEntity.description.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            android.R.id.home -> {

                    mActivity?.onBackPressed()

                    hideKeyboard()
                true
            }
            R.id.action_save ->{
                if (mNoteEntity != null && validateFields(mBinding.tilDate, mBinding.tilName) ){

                    with(mNoteEntity!!){
                        name = mBinding.etName.text.toString().trim()
                        date = mBinding.etDate.text.toString().trim()
                        description = mBinding.etDescription.text.toString().trim()
                        image = try {
                            mPhotoSelectedString!!
                        }catch (e: Exception){
                            "Error"
                        }


                    }

                    doAsync {
                        if(mIsEditMode) NoteAplication.database.noteDao().updateNote(mNoteEntity!!)
                        else  mNoteEntity!!.id = NoteAplication.database.noteDao().addNote(mNoteEntity!!)

                        uiThread {

                            hideKeyboard()

                            if(mIsEditMode){
                                mNoteFragment?.updateNote(mNoteEntity!!)
                                Snackbar.make(mBinding.root, R.string.edit_note_massage_success, Snackbar.LENGTH_SHORT).show()
                                mActivity?.onBackPressed()
                                mActivity?.onBackPressed()
                            }else {
                                mNoteFragment?.addNote(mNoteEntity!!)

                                Toast.makeText( mActivity, R.string.add_note_massage_success , Toast.LENGTH_SHORT).show()
                                mActivity?.onBackPressed()
                            }



                        }
                    }

                }
                true
            }

            else-> return super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean{
        var isValid = true

        for (textField in textFields){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                textField.editText?.requestFocus()
                isValid = false
            }else textField.error = null
        }

        if(!isValid) Snackbar.make(mBinding.root, R.string.edit_store_message_valid, Snackbar.LENGTH_SHORT).show()

        return isValid
    }


    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null){
            imm.hideSoftInputFromWindow(this.requireView().windowToken, 0)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {

        if(mIsEditMode){
            mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            mActivity?.supportActionBar?.title = getString(R.string.note)
            mNoteFragment?.hideFab(true)
        }else {
            mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            mActivity?.supportActionBar?.title = getString(R.string.app_name)
            mNoteFragment?.hideFab(true)
        }



        setHasOptionsMenu(false)
        super.onDestroy()
    }


    private fun setupActionBar() {

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if (mIsEditMode) getString(R.string.edit_note_title_edit)
        else getString(R.string.edit_note_title_add)

        setHasOptionsMenu(true)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_GALLERY)
        mBinding.btnSelect.visibility = View.INVISIBLE
        mBinding.tvAddImage.visibility = View.INVISIBLE
    }

  private fun dispatchTakePictureIntent(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {

            var imageFile: File? = null

            try {
                imageFile = createImageFile()
            }catch (e: IOException){
                Log.d("Image", e.toString())
            }

            if (imageFile != null){

                mPhotoSelectedUri = FileProvider.getUriForFile(this.requireContext(), "com.rrstudio.notasfast2.fileprovider", imageFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoSelectedUri)

                startActivityForResult(takePictureIntent, RC_CAMERA)
                mBinding.btnSelect.visibility = View.INVISIBLE
                mBinding.tvAddImage.visibility = View.INVISIBLE

            }
        }catch (e: ActivityNotFoundException){
            Toast.makeText(this.context, getString(R.string.camera_not_found), Toast.LENGTH_SHORT).show()
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (requestCode == RC_CAMERA){
                mPhotoSelectedBitmap = BitmapFactory.decodeFile(mPhotoSelectedString)
                mBinding.imgPhoto.setImageBitmap(mPhotoSelectedBitmap)


            }

        }
    }


    private fun createImageFile(): File {

        var imageName: String = "photo_"
        var directory: File? = mActivity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image : File = File.createTempFile(imageName, ".jpg", directory)

        mPhotoSelectedString = image.absolutePath
        return image
    }

}