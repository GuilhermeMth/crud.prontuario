package crud.prontuario.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

	private final static String ARQUIVO = "config.properties";
	private final static Properties PROP = new Properties();
	
	static {
		load();
	}
	
	public static void load() {
		try (InputStream input = ConfigLoader.class.getResourceAsStream("/" + ARQUIVO)) {
			if (input == null) {
				System.err.println("Arquivo " + ARQUIVO + " não encontrado no classpath!");
				return;
			}
			PROP.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getValor(String chave) {
		return PROP.getProperty(chave);
	}
}