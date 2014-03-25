package com.enablens.dfa.base;

public class NetUtil {
    /**
     * Takes a string and parses it to see if it is a valid IPV4 address.
     * 
     * Copied verbatim from:
     * https://github.com/netty/netty/blob/master/common/src/main/java/io/netty/
     * util/NetUtil.java
     * 
     * @param ip
     *            the string representation of an IPv4
     *            address in dotted decimal notation.
     * 
     * @return true, if the string represents an IPV4 address in dotted
     *         notation, false otherwise
     */
    public static boolean isValidIpV4Address(final String ip) {

        int periods = 0;
        int i;
        int length = ip.length();

        if (length > 15) {
            return false;
        }
        char c;
        StringBuilder word = new StringBuilder();
        for (i = 0; i < length; i++) {
            c = ip.charAt(i);
            if (c == '.') {
                periods++;
                if (periods > 3) {
                    return false;
                }
                if (word.length() == 0) {
                    return false;
                }
                if (Integer.parseInt(word.toString()) > 255) {
                    return false;
                }
                word.delete(0, word.length());
            } else if (!Character.isDigit(c)) {
                return false;
            } else {
                if (word.length() > 2) {
                    return false;
                }
                word.append(c);
            }
        }

        if (word.length() == 0 || Integer.parseInt(word.toString()) > 255) {
            return false;
        }
        if (periods != 3) {
            return false;
        }
        return true;
    }

}
