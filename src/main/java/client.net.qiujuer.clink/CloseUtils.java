package client.net.qiujuer.clink;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils {
    public static void close(Closeable...closeables){
        if(closeables==null){
            return;
        }else {
            for (Closeable closeable : closeables) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
