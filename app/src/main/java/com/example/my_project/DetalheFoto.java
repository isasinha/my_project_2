package com.example.my_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class DetalheFoto extends AppCompatActivity {

    private ImageView posterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_foto);
        Intent it = getIntent();
        byte [] bytes = it.getByteArrayExtra("pic");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        posterImageView = findViewById(R.id.posterImageView);
        posterImageView.setImageBitmap(bitmap);
    }

    public void exibeFotos(View v){
        Intent intent = new Intent(this, FotosActivity.class);
        startActivity(intent);
    }

    public void exibeTexto(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
