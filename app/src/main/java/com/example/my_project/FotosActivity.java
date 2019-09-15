package com.example.my_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FotosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotos);
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
        Intent intent = new Intent(this, DetalheFoto.class);
        startActivity(intent);
    }

}
