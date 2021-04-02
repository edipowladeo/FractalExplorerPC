package com.example.fractal.windows

import com.example.fractal.CoordenadasTela
import com.example.fractal.Janela
import com.example.fractal.Relogio
import com.example.fractal.android.ThreadProcessamento
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL32 as OGL
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

class HelloWordWindow : DesenhistaDeCelulas() {
    // The window handle

    private var window: Long = 0

    val janela = Janela(GerenciadorDeImplementacoesPC())

    var XX = 0f;
    var YY = 0f;
    var largura: Float = 2000.0f
    var altura: Float = 1500.0f
    var textoDebug = StringBuilder()


    private lateinit var myGlProgram: MyGLProgramPC
    var mProgramHandle = 0
    var mTextureUniformHandle = 0
    var mWindowSizeUniformHandle = 0
    var mTextureBufferuniformhandle = 0

    fun run() {
        println("Hello LWJGL " + Version.getVersion() + "!")
        init()
        loop()

        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)!!.free()
    }

    private fun init() {

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }

        // Configure GLFW
        GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE) // the window will be resizable

        // Create the window
        window = GLFW.glfwCreateWindow(largura.toInt(), altura.toInt(), "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL)
        if (window == MemoryUtil.NULL) throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(
            window
        ) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) GLFW.glfwSetWindowShouldClose(
                window,
                true
            ) // We will detect this in the rendering loop
        }
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())

            // Center the window
            GLFW.glfwSetWindowPos(
                window,
                (vidmode!!.width() - pWidth[0]) / 2,
                (vidmode.height() - pHeight[0]) / 2
            )
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window)
        // Enable v-sync
        GLFW.glfwSwapInterval(1)

        // Make the window visible
        GLFW.glfwShowWindow(window)



        GL.createCapabilities()
        /* */


        myGlProgram = MyGLProgramPC(
                "src/shaders/TextureWrapperVertex.glsl",
                "src/shaders/RawTextureFragment.glsl"
        )
        // Set the background clear color to gray.
        OGL.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        mProgramHandle = myGlProgram.getProgramHandle()
        handlePosicaoVertices = OGL.glGetAttribLocation(mProgramHandle, "a_RectangleVertices");
        mTextureUniformHandle = OGL.glGetUniformLocation(mProgramHandle, "u_Texture");
        mWindowSizeUniformHandle = OGL.glGetUniformLocation(mProgramHandle, "u_WindowSize");
        handlePosicaoCelulas = OGL.glGetUniformLocation(mProgramHandle, "u_SpritePosition")
        handleEscalaDaPaleta = OGL.glGetUniformLocation(mProgramHandle, "u_escalaPaleta")
        handleTempo = OGL.glGetUniformLocation(mProgramHandle, "u_tempo")
        mTextureBufferuniformhandle =  OGL.glGetUniformLocation(mProgramHandle, "u_TextureBuffer")



        // Tell OpenGL to use this program when rendering.
        OGL.glUseProgram(myGlProgram.getProgramHandle());
        //redundante???
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        OGL.glActiveTexture(OGL.GL_TEXTURE1);
        OGL.glUniform1i(mTextureUniformHandle, 0);

          OGL.glActiveTexture(OGL.GL_TEXTURE0);
        OGL.glUniform1i(mTextureBufferuniformhandle, 0);

        val threadProcessamento = List<ThreadProcessamento>(12){ ThreadProcessamento(janela) }
        threadProcessamento.forEach{it.start()}
    }

    private fun loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!GLFW.glfwWindowShouldClose(window)) {
            OGL.glClear(OGL.GL_COLOR_BUFFER_BIT or OGL.GL_DEPTH_BUFFER_BIT) // clear the framebuffer

            desenhar()

            GLFW.glfwSwapBuffers(window) // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            GLFW.glfwPollEvents()
        }
    }

    fun desenhar(){
        OGL.glUniform2f(mWindowSizeUniformHandle,largura,altura)


        janela.let { janela ->
            val tempoinicio = janela.relogio.getCurrentTimeMs()

            if (janela.ExibirInformacoesDaJanelaEmOverlay) atualizarTexto();
            //janela.setDimensaoDaJanelaDeSaida(  CoordenadasTela(   largura.toDouble(),     altura.toDouble()   )            )
            janela.desenharCelulas(this)

            //     Log.i("Tempo", "${SystemClock.uptimeMillis()-tempoinicio} millisseconds")
            //janela.TarefasDesalocarTextura.executarPrimeira()
            //janela.TarefasAlocarTextura.executarPrimeira()
            //  tempoinicio = SystemClock.uptimeMillis()
            while (janela.relogio.getCurrentTimeMs() - tempoinicio < 15) {
                janela.TarefasDesalocarTextura.executarPrimeira()
                janela.TarefasAlocarTextura.executarPrimeira()
            }
        }
    }

    private fun atualizarTexto() {
        textoDebug.clear()
        val coordenadasTela = CoordenadasTela(XX.toDouble(), YY.toDouble())
        coordenadasTela.x -= largura / 2
        coordenadasTela.y -= altura / 2
        janela?.let {
            textoDebug.append("\nTouch " + coordenadasTela.toString())
            textoDebug.append("\nPlano " + it.getCoordenadasPlano(coordenadasTela))
            textoDebug.append("\n" + it.toString())
        }
     //   ouvinte?.recieveText(textoDebug.toString())

        /* Thread(Runnable {
          ouvinte?.recieveText(textoDebug.toString())

      }).start()*/
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            HelloWordWindow().run()
        }

        private var textureHandle = IntArray(1) { 0 }

        fun bindTexture() {
            OGL.glBindTexture(OGL.GL_TEXTURE_2D, textureHandle[0])
        }
    }
}

//todo:redimensionar janela
/*
@RequiresApi(Build.VERSION_CODES.N)
override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    GLES32.glViewport(0, 0, width, height)

    largura = width.toFloat()
    altura = height.toFloat()
    janela?.let{
        it.setDimensaoDaJanelaDeSaida(CoordenadasTela(largura.toDouble(),altura.toDouble()))
        //Log.i("Renderer", "janela ${largura} x ${altura}")

    }
    val ratio = width.toFloat() / height
    GLES32.glUniform2f(mWindowSizeUniformHandle,width.toFloat(),height.toFloat())

    /** toda vez que onSurfaceChanged é chamado, há destruição do contexto OpenCL, e portanto de todas as texturas*/
    /**como há persistência da janela, é preciso recriar todas as texturas*/
    janela?.let{
        it.camadas.forEach { (t, camada) ->
            camada.Celulas.forEachIndexed{i , colunas ->
                colunas.forEachIndexed(){j, linhas ->
                    linhas.run{
                        this.solicitarGeracaoDeTexturaGL()
                    }
                }
            }
        }
    }

}*/