package network;

public class ConnectionCodes {
    public static final String serverIP = "192.168.188.55";
    public static final int port = 55555;
    public static final byte BEGIN = 1;  	//deprecated
    public static final byte END = 18;
    public static final byte REGISTER = 15;	// dec 10 -> grey code -> dec 15
    public static final byte REQUEST = 20;	// dec 10, 13 = \n\r
    public static final byte MAPPART = 25;
    public static final byte RETRY = 30;    // wiederaufnahme von downloads
    public static final byte SPEED = 40;    // zum übertragen der Geschwindgkeit
    
    public static final byte MAP = 100;		// general request code for maps
    public static final byte MAP_MV = 101;	// Mecklenburg-Vorpommern
    public static final byte MAP_SH = 102;	// Schleswig-Holstein
    public static final byte MAP_NS = 103;	// Niedersachsen
    public static final byte MAP_HH = 104;	// Hamburg
    public static final byte MAP_HB = 105;	// Bremen
    public static final byte MAP_BR = 106;	// Brandenburg
    public static final byte MAP_B = 107;	// Berlin
    public static final byte MAP_SA = 108;	// Sachsen Anhalt
    public static final byte MAP_NRW = 109;	// Nordrhein-Westfalen
    public static final byte MAP_HS = 110;	// Hessen
    public static final byte MAP_RP = 111;	// Rheinland-Pfalz
    public static final byte MAP_SL = 112;	// Saarland
    public static final byte MAP_TH = 113;	// Thürigen
    public static final byte MAP_SS = 114;	// Sachsen
    public static final byte MAP_BW = 115;	// Baden-Württemberg
    public static final byte MAP_BA = 116;	// Bayern
    public static final byte MAP_GER = 117;	// Deutschland, komplett
    
    // TODO bessere Werte
}
