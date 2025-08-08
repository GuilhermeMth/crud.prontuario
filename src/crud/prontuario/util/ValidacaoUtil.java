package crud.prontuario.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ValidacaoUtil {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

    static {
        SDF.setLenient(false);
    }

    public static boolean isVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean isNomeValido(String nome) {
        return nome != null && nome.trim().matches("^[\\p{L}][\\p{L}\\s]{1,98}[\\p{L}]$");
    }

    public static boolean isCPFValido(String cpf) {
        if (cpf == null) return false;

        cpf = cpf.replaceAll("\\D", "");

        if (!cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) return false;

        int soma = 0;
        for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - '0') * (10 - i);
        int dig1 = (soma % 11 < 2) ? 0 : 11 - (soma % 11);

        soma = 0;
        for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - '0') * (11 - i);
        int dig2 = (soma % 11 < 2) ? 0 : 11 - (soma % 11);

        return dig1 == (cpf.charAt(9) - '0') && dig2 == (cpf.charAt(10) - '0');
    }

    public static boolean isFormatoCPF(String cpf) {
        return cpf != null && cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }

    public static boolean isDataValida(String data) {
        if (data == null || !data.matches("\\d{2}/\\d{2}/\\d{4}")) return false;
        try {
            SDF.parse(data);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
