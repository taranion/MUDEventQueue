module org.prelle.mudevents {
    exports org.prelle.mudevents;    
    exports org.prelle.mudevents.ansi;
    exports org.prelle.mudevents.telnet;
    exports org.prelle.mudevents.util;
    
	requires lombok;
	requires transitive org.prelle.telnet;
	requires org.prelle.libansi;
}
