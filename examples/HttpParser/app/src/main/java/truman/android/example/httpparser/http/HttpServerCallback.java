package truman.android.example.httpparser.http;

import android.os.Bundle;

public interface HttpServerCallback {
    void onGet(Bundle request);
    void onPost(Bundle request, byte[] buff);
}
