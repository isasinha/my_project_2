package com.example.my_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DetalheFoto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhe_foto);
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
