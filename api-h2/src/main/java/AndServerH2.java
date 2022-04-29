import android.content.Context;

import androidx.annotation.NonNull;

import com.yanzhenjie.andserver.H2Server;
import com.yanzhenjie.andserver.server.async.H2ReverseProxyServer;
import com.yanzhenjie.andserver.server.async.H2WebServer;

public class AndServerH2 {

    /**
     * Create a builder for the asynchronous web server.
     *
     * @return {@link H2Server.Builder}.
     */
    @NonNull
    public static H2Server.Builder<?, ?> webServerH2(@NonNull Context context) {
        return webServerH2(context, "default");
    }

    /**
     * Create a builder for the asynchronous web server.
     *
     * @param group group name.
     * @return {@link H2Server.Builder}.
     */
    @NonNull
    public static H2Server.Builder<?, ?> webServerH2(@NonNull Context context,
                                                     @NonNull String group) {
        return H2WebServer.newBuilder(context, group);
    }

    /**
     * Create a builder for the asynchronous reverse proxy server.
     *
     * @return {@link H2Server.ProxyBuilder}.
     */
    @NonNull
    public static H2Server.ProxyBuilder<?, ?> proxyServerH2() {
        return H2ReverseProxyServer.newBuilder();
    }

}
