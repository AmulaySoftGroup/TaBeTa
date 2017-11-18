package ir.amulay.tabeta.curl;



import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;
import android.util.Log;

public class FBO {
	int[] fb, renderTex;
	int texW;
	int texH;

	public FBO(int width, int height) {
		texW = width;
		texH = height;
		fb = new int[1];
		renderTex = new int[1];

	}

	public void BeforeStart(GL10 gl) {
		// generate
		((GL11ExtensionPack) gl).glGenFramebuffersOES(1, fb, 0);

	} 
	public void setup(GL10 gl){
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glGenTextures(1, renderTex, 0);// generate texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, renderTex[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);
		// texBuffer =
		// ByteBuffer.allocateDirect(buf.length*4).order(ByteOrder.nativeOrder()).asIntBuffer();
		// gl.glTexEnvf(GL10.GL_TEXTURE_ENV,
		// GL10.GL_TEXTURE_ENV_MODE,GL10.GL_MODULATE);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, texW, texH, 0,
				GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		//Log.e("Creating", "Creating Texture = " +renderTex[0]);
	}

	public boolean RenderStart(GL10 gl) {
		// Log.d("TextureAndFBO", ""+renderTex[0] + " And " +fb[0]);
		// Bind the framebuffer
		((GL11ExtensionPack) gl).glBindFramebufferOES(
				GL11ExtensionPack.GL_FRAMEBUFFER_OES, fb[0]);

		// specify texture as color attachment
		((GL11ExtensionPack) gl).glFramebufferTexture2DOES(
				GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D,
				renderTex[0], 0);
		// Log.e("TEXTURE ID =", "Texture ID  =" +renderTex[0] );
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.d("err", "FIRST Background Load GLError: " + error + "      ");
		}
		int status = ((GL11ExtensionPack) gl)
				.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
		if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
			Log.d("err", "SECOND Background Load GLError: " + status + "      ");
			;
			return true;
		}
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		return true;
	}

	public void RenderEnd(GL10 gl) {
		((GL11ExtensionPack) gl).glBindFramebufferOES(
				GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	public int getTexture() {
		return renderTex[0];
	}

	public int getFBO() {
		return fb[0];
	}

}