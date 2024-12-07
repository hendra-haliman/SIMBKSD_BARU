package id.go.jakarta.bksd.dokumen.captcha;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;
import cn.apiclub.captcha.noise.CurvedLineNoiseProducer;
import cn.apiclub.captcha.text.producer.DefaultTextProducer;
import cn.apiclub.captcha.text.renderer.ColoredEdgesWordRenderer;
import cn.apiclub.captcha.text.renderer.DefaultWordRenderer;

// https://github.com/abdamah/SimpleCaptchainSpringboot/blob/master/SpringBootSimpleCaptcha/src/main/java/com/riigsoft/captcha/CaptchaUtils.java
public interface CaptchaUtils {
	// 1. creating captcha	
	public static Captcha createCaptcha(int width, int height) {
		return new Captcha.Builder(width, height)
				.addBackground(new GradiatedBackgroundProducer(Color.CYAN, Color.GREEN))
				.addText(new DefaultTextProducer(), new DefaultWordRenderer())
                //.addText(new DefaultTextProducer(), new ColoredEdgesWordRenderer())
                //.addNoise(new CurvedLineNoiseProducer(Color.BLACK, 3.0F))
				.addNoise(new CurvedLineNoiseProducer(Color.RED, 2.5F)).build();
	}

	// 2. creating captcha image
	public static String encodeBase64(Captcha captcha) {
		String image= null;
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(captcha.getImage(), "png", outputStream);
			byte[] arr = Base64.getEncoder().encode(outputStream.toByteArray());
			image = new String(arr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
