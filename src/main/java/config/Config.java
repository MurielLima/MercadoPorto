package config;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;

public class Config {

    public static final ResourceBundle i18n = ResourceBundle.getBundle(String.format("i18n.Bundle_%s", "pt_BR"));
    public static final DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    public static final NumberFormat nf
            = NumberFormat.getInstance(Locale.getDefault());
    public static final char separadorDecimal
            = new DecimalFormatSymbols(Locale.getDefault(Locale.Category.FORMAT)).getDecimalSeparator();

    public static final char INCLUIR = 'I';
    public static final char ALTERAR = 'A';
    public static final char EXCLUIR = 'E';

}
