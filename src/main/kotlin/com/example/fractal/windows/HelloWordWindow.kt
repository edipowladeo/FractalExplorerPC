package com.example.fractal.windows

import com.example.fractal.CoordenadasTela
import com.example.fractal.Janela
import com.example.fractal.UniformHandle
import com.example.fractal.WindowHandle
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL32 as OGL
import org.lwjgl.system.MemoryUtil


class HelloWordWindow : DesenhistaDeCelulas() {
    private var dimensoesJanela = CoordenadasTela(800.0,600.0)

    private var windowHandle: WindowHandle = 0

    private val janelaFractal = Janela(
            GerenciadorDeImplementacoesPC(),
            dimensoesJanela
    )

    private val textoDebug = StringBuilder()

    private lateinit var myGlProgram: MyGLProgramPC
    private var mProgramHandle: UniformHandle = 0
    private var mWindowSizeUniformHandle: UniformHandle = 0
    private var mIteracoesTexBufferUniformHandle: UniformHandle = 0
    private var mPaletaTexBufferUniformHandle: UniformHandle = 0
    private var mDimSpriteUniformHandle: UniformHandle = 0

    init{
        run()
    }

    fun run() {
        println("Hello LWJGL " + Version.getVersion() + "!")
        initOpenGLWindow()
        setCallBacks()
        initOpenGlProgram()
        configureUniforms()

        while (!GLFW.glfwWindowShouldClose(windowHandle)) {
            loop()
        }

        // Free Resources
        janelaFractal.liberarRecursos()

        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(windowHandle)
        GLFW.glfwDestroyWindow(windowHandle)

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)!!.free()
    }

    private fun setCallBacks() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(windowHandle) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) GLFW.glfwSetWindowShouldClose(
                    window,
                    true
            ) // We will detect this in the rendering loop
        }

        GLFW.glfwSetWindowSizeCallback(windowHandle){
         A: Long, x: Int, y: Int ->
            // println("A: $A x: $x y:$y")
            dimensoesJanela = CoordenadasTela(x,y)
            OGL.glUniform2f(mWindowSizeUniformHandle, x.toFloat(), y.toFloat())
            janelaFractal.setDimensaoDaJanelaDeSaida(CoordenadasTela(x.toDouble(),y.toDouble()))
            OGL.glViewport(0,0,x,y)

        }
    }

    private fun initOpenGLWindow() {
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(GLFW.glfwInit()) { throw RuntimeException("Unable to initialize GLFW") }

        val resolucaoDesktop = getDesktopResolution()

        // Configure GLFW
        GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden right after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE) // the window will be resizable


        // Create the window
        windowHandle = GLFW.glfwCreateWindow(dimensoesJanela.x.toInt(), dimensoesJanela.y.toInt(), "Mandelbrot Set!", MemoryUtil.NULL, MemoryUtil.NULL)
        if (windowHandle == MemoryUtil.NULL) throw RuntimeException("Failed to create the GLFW window")

        val tamJanela = getOGLWindowResolution(windowHandle)

        // Center the window
        GLFW.glfwSetWindowPos(
                windowHandle,
                (resolucaoDesktop.x - tamJanela.x) / 2,
                (resolucaoDesktop.y - tamJanela.y) / 2
        )

        janelaFractal.setDimensaoDaJanelaDeSaida(CoordenadasTela(tamJanela.x.toDouble(),tamJanela.y.toDouble()))

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(windowHandle)
        // Enable v-sync
        GLFW.glfwSwapInterval(1)

        // Make the window visible
        GLFW.glfwShowWindow(windowHandle)

        GL.createCapabilities()

        OGL.glClearColor(0.5f, 0.9f, 0.5f, 0.5f);
    }

    private fun initOpenGlProgram() {
        myGlProgram = MyGLProgramPC(
                "src/shaders/TextureWrapperVertex.glsl",
                "src/shaders/RawTextureFragment.glsl"
        )
        mProgramHandle = myGlProgram.getProgramHandle()
        OGL.glUseProgram(mProgramHandle)
    }

    private fun configureUniforms() {
        handlePosicaoVertices = OGL.glGetAttribLocation(mProgramHandle, "a_RectangleVertices");
        mWindowSizeUniformHandle = OGL.glGetUniformLocation(mProgramHandle, "u_WindowSize");
        handlePosicaoCelulas = OGL.glGetUniformLocation(mProgramHandle, "u_SpritePosition")
        handleEscalaDaPaleta = OGL.glGetUniformLocation(mProgramHandle, "u_escalaPaleta")
        handleTempo = OGL.glGetUniformLocation(mProgramHandle, "u_tempo")
        mIteracoesTexBufferUniformHandle = OGL.glGetUniformLocation(mProgramHandle, "u_Iteracoes")
        mPaletaTexBufferUniformHandle = OGL.glGetUniformLocation(mProgramHandle, "u_Paleta")
        mDimSpriteUniformHandle = OGL.glGetUniformLocation(mProgramHandle, "u_dimSprite")

        OGL.glUniform1i(mIteracoesTexBufferUniformHandle, 3);
        OGL.glUniform1i(mPaletaTexBufferUniformHandle, 4);
        OGL.glUniform2i(mDimSpriteUniformHandle, janelaFractal.tamSprite.x, janelaFractal.tamSprite.y)
        OGL.glUniform2f(mWindowSizeUniformHandle, dimensoesJanela.x.toFloat(), dimensoesJanela.y.toFloat())
    }

    private fun loop() {
        OGL.glClear(OGL.GL_COLOR_BUFFER_BIT or OGL.GL_DEPTH_BUFFER_BIT) // clear the framebuffer

        desenhar()

        GLFW.glfwSwapBuffers(windowHandle) // swap the color buffers

        GLFW.glfwPollEvents()
    }

    private fun desenhar() {
        janelaFractal.let { janela ->
            val tempoInicio = janela.relogio.getCurrentTimeMs()

            if (janela.ExibirInformacoesDaJanelaEmOverlay) atualizarTexto();
            //janela.setDimensaoDaJanelaDeSaida(  CoordenadasTela(   largura.toDouble(),     altura.toDouble()   )            )
            janela.desenharCelulas(this)

            //     Log.i("Tempo", "${SystemClock.uptimeMillis()-tempoinicio} millisseconds")
            //janela.TarefasDesalocarTextura.executarPrimeira()
            //janela.TarefasAlocarTextura.executarPrimeira()
            //  tempoinicio = SystemClock.uptimeMillis()
            while (janela.relogio.getCurrentTimeMs() - tempoInicio < 15) {
                janela.tarefasDesalocarTextura.executarPrimeira()
                janela.tarefasAlocarTextura.executarPrimeira()
            }
        }
    }

    private fun atualizarTexto() {
        textoDebug.clear()
        val coordenadasTela = dimensoesJanela
        coordenadasTela.x -= dimensoesJanela.x / 2
        coordenadasTela.y -= dimensoesJanela.y / 2
        janelaFractal.let {
            textoDebug.append("\nTouch " + coordenadasTela.toString())
            textoDebug.append("\nPlano " + it.getCoordenadasPlano(coordenadasTela))
            textoDebug.append("\n" + it.toString())
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            HelloWordWindow()
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