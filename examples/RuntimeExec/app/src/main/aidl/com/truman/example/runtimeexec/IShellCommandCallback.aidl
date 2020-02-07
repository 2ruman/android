// IShellCommandCallback.aidl
package com.truman.example.runtimeexec;

interface IShellCommandCallback {
    void onReadLine(in String line);
    void onFailure(in String errorMessage);
    void onReadPage(in byte[] page, int length);
    /*
        Truman : Do we need asynchronous callbacks? Hmm... Just in case!
    */
    oneway void onReadLineAsync(in String line);
    oneway void onFailureAsync(in String errorMessage);
    oneway void onReadPageAsync(in byte[] page, int length);
}
