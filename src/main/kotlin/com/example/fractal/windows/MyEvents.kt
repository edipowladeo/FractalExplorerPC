package com.example.fractal.android



class MyEvents() {


    private val DEBUG_TAG = "Gestos: "
    //private var detectorDeGestos: GestureDetectorCompat

    //val renderer: RendererFractal

    init {
        /*
        // Create an OpenGL ES 2.0 context

//        renderer = RendererFractal(context)
 //       setRenderer(renderer)

        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        detectorDeGestos = GestureDetectorCompat(context, this)
        detectorDeGestos.setOnDoubleTapListener(this)*/
    }


/*
    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        var mScaleFactor = 1f
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            invalidate()
            return true
        }
    }*/

    /*
    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
*/


    /*
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(event)
        renderer.janela?.dividirDeltaPor(scaleListener.mScaleFactor)
        scaleListener.mScaleFactor = 1f; // Reseta
        return if (detectorDeGestos.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }


     */


    /*
    override fun onScroll(
        event1: MotionEvent,
        event2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        ////Log.d(DEBUG_TAG, "onScroll: $event1 $event2")
        renderer.janela?.moverCamera(distanceX,distanceY)
        return true
    }
*/

    /*
    override fun onDown(event: MotionEvent): Boolean {
        //Log.d(DEBUG_TAG, "onDown: $event")
        return true
    }
*/


    /*

    override fun onFling(
            event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        //Log.d(DEBUG_TAG, "onFling: $event1 $event2")
        return true
    }

    override fun onLongPress(event: MotionEvent) {
        //Log.d(DEBUG_TAG, "onLongPress: $event")
    }

    override fun onShowPress(event: MotionEvent) {
        //Log.d(DEBUG_TAG, "onShowPress: $event")
    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        //Log.d(DEBUG_TAG, "onSingleTapUp: $event")
        return true
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        //Log.d(DEBUG_TAG, "onDoubleTap: $event")
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        //Log.d(DEBUG_TAG, "onDoubleTapEvent: $event")
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        //Log.d(DEBUG_TAG, "onSingleTapConfirmed: $event")
        return true
    }
}

/*   override fun onTouchEvent(e: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(e)
        var currentPointerId = e.getPointerId(1)
        //currentPointerId = e.getPointerProperties()

        if (previouspointerID ==currentPointerId){
          val x: Float = e.x
          val y: Float = e.y

          val dx = renderer.XX - x
          val dy = renderer.YY - y


          //TODO: melhorar o metodo, identificar id do toque
          val max = 50f
          /* if (dx> max) dx = max
        if (dy> max) dy = max
        if (dx< -max) dx = -max
        if (dy< -max) dy = -max
        */
          if (dx * dx + dy * dy < max * max) renderer.janela?.moverCamera(dx * 2, dy * 2)

      }
            previouspointerID = currentPointerId

    renderer.janela?.alterarDelta(1/mScaleFactor)
        mScaleFactor = 1f
        renderer.XX = x
        renderer.YY = y

        //previousX = x
        //previousY = y

        //TODO:Verificar a utilidade e necessidade
     //   requestRender();


        return true
    }*/

//override fun onD




*/
}