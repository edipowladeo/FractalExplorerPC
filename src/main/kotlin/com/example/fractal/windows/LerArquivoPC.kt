package com.example.fractal.windows

import java.io.File

//todo: Criar interface openfile
fun lerArquivoPC (fileName: String): String
        = File(fileName).readText(Charsets.UTF_8)
