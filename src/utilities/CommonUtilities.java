package utilities;

/**
 *
 * @author dangxuananh1997
 */
public class CommonUtilities {
    
    public static int convertPriceHangchinhhieu(String priceString) {
        if (priceString.equals("Liên hệ")) {
            return 0;
        }
        
        // remove 'đ' character
        priceString = priceString.substring(0, priceString.length() - 1);
        
        while (priceString.contains(",")) {
            int pos = priceString.indexOf(",");
            priceString = priceString.substring(0, pos) + priceString.substring(pos + 1, priceString.length());
        }
        
        return Integer.parseInt(priceString);
    }
    
    public static int convertPriceXgear(String price) {
        return 0;
    }
    
    public static String addCloseTagToLine(String line, String tag) {
        int openTagPos = line.indexOf("<" + tag);
        boolean closeTagProperly = line.indexOf("/>", openTagPos) > 0;
        
        if (!closeTagProperly) {
            int closeTagPos = line.indexOf(">", openTagPos);
            line = line.substring(0, closeTagPos) + "/" + line.substring(closeTagPos, line.length());
        }
        
        return line;
    }
    
}
