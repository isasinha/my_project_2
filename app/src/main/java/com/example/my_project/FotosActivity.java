package com.example.my_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class FotosActivity extends AppCompatActivity  implements View.OnClickListener{

    private RecyclerView fotoRecyclerView;
    private FotoAdapter adapter;
    private List<Foto> fotos;
    private CollectionReference mMsgsReference;
    private DocumentReference tagMsgsReference;
    private CollectionReference subCOLtagMsgsReference;
    private EditText tagEditText;
    private TextView tagSelecionadaTextView;
    private String tag;
    private static final int REQ_CODE_CAMERA = 1001;
    ImageView pictureImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotos);
        fotoRecyclerView = findViewById(R.id.fotoRecyclerView);
        fotos = new ArrayList<>();
        adapter = new FotoAdapter(fotos, this);
        fotoRecyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
//        gridLayoutManager.setReverseLayout(true);
//       gridLayoutManager.setStackFromEnd(true);
        fotoRecyclerView.setLayoutManager(gridLayoutManager);
        tagEditText = findViewById(R.id.tagEditText);
        tagSelecionadaTextView = findViewById(R.id.tagSelecionadaTextView);
        pictureImageView = (ImageView) findViewById(R.id.pictureImageView);
    }

    private void setupFirebase (){
        if(tag != "" && tag != null) {
            mMsgsReference = FirebaseFirestore.getInstance().collection("fotos");
            tagMsgsReference = mMsgsReference.document(tag);
            subCOLtagMsgsReference = tagMsgsReference.collection("envios");
            getRemoteFotos();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getRemoteFotos (){
        subCOLtagMsgsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(
                    @Nullable QuerySnapshot queryDocumentSnapshots,
                    @Nullable FirebaseFirestoreException e)
            {
                fotos.clear();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    Foto f = doc.toObject(Foto.class);
                    String encoded = f.getEncodedFoto();
                    byte[] decodedBytes = Base64.decode(
                            encoded.substring(encoded.indexOf(",")  + 1),
                            Base64.DEFAULT
                    );
                    Bitmap picture = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    Foto foto = new Foto(f.getData(), picture);
                    fotos.add(foto);
                }
                Collections.sort(fotos);
                Collections.reverse(fotos);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void buscarTag (View view){
        int lba = tagEditText.getText().toString().length();
        if(lba > 0) {
            tag = tagEditText.getText().toString();
            tagSelecionadaTextView.setText(tag);
            tagEditText.setText("");
            setupFirebase();
            esconderTeclado(view);
        }else{
            Toast.makeText(this, getString(R.string.digitar_tag), Toast.LENGTH_SHORT).show();
        }
    }

    public void tirarFoto(View view){
        if(tag != null && tag != "") {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //dá pra tirar foto?
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQ_CODE_CAMERA);
            } else
                Toast.makeText(this, getString(R.string.cant_take_pic), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.sem_tag), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPicture (Bitmap picture){
        mMsgsReference = FirebaseFirestore.getInstance().collection("fotos");
        tagMsgsReference = mMsgsReference.document(tag);
        subCOLtagMsgsReference = tagMsgsReference.collection("envios");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] bytes = baos.toByteArray();
        String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
        Foto f = new Foto(new Date(), encoded);
        subCOLtagMsgsReference.add(f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode == REQ_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap picture = (Bitmap)data.getExtras().get("data");
                uploadPicture(picture);
            }
            else {
                Toast.makeText(this, getString(R.string.no_pic_taken), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void excluirFoto(final int position){

        new AlertDialog.Builder(this)
            .setTitle("Excluir foto")
            .setMessage("Tem certeza que deseja excluir essa foto?")
            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    fotos.remove(position);
                    adapter.notifyDataSetChanged();
                }

            })
            .setNegativeButton("não", null)
            .show();
    }

    @Override
    public void onClick(View view) {
        pictureImageView = findViewById(R.id.pictureImageView);
        if(view == pictureImageView) {
            pictureImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    exibeDetalheFoto(v);
                }
            });
            pictureImageView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    exibeDetalheFoto(v);
                    return true;
                }
            });
        }

    }

    private void esconderTeclado (View v){
        InputMethodManager ims = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        ims.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void exibeFotos(View v){
        Intent intent = new Intent(this, FotosActivity.class);
        startActivity(intent);
    }

    public void exibeTexto(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void exibeDetalheFoto(View v){
        ImageView pictureImageView;
        pictureImageView = v.findViewById(R.id.pictureImageView);
        Drawable imagemDraw = pictureImageView.getDrawable();
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imagemDraw);
        Bitmap picture = bitmapDrawable .getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] bytes = baos.toByteArray();
        Intent intent = new Intent(this, DetalheFoto.class );
        intent.putExtra("pic", bytes);
        startActivity(intent);
    }
}

class FotoViewHolder extends RecyclerView.ViewHolder {
    ImageView pictureImageView;

    FotoViewHolder(View v) {
        super(v);
        this.pictureImageView = v.findViewById(R.id.pictureImageView);
    }
}

class FotoAdapter extends RecyclerView.Adapter<FotoViewHolder>{
    private List<Foto> fotos;
    private Context context;
    FotoAdapter (List<Foto> fotos, Context context){
        this.fotos = fotos;
        this.context = context;
    }
    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item_fotos, parent,false);
        return new FotoViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        Foto f = fotos.get(position);
        holder.pictureImageView.setImageBitmap(f.getPicture());
    }
    @Override
    public int getItemCount() { return fotos.size(); }
}