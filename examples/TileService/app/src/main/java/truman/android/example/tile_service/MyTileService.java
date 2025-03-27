package truman.android.example.tile_service;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.TextView;

import java.util.function.Consumer;

public class MyTileService extends TileService {

    private static final String TAG = MyTileService.class.getSimpleName() + ".2ruman";

    public void onTileAdded() {
        Log.d(TAG, "onTileAdded()");
    }

    public void onTileRemoved() {
        Log.d(TAG, "onTileRemoved()");
    }

    public void onStartListening() {
        Log.d(TAG, "onStartListening()");
    }

    public void onStopListening() {
        Log.d(TAG, "onStopListening()");
    }

    public void onClick() {
        Log.d(TAG, "onClick() - is secure? " + isSecure());
        if (isLocked()) {
            unlockAndRun();
            return;
        }
        getAndToggle((state) -> Log.d(TAG, "onClick() - state? " + state));
    }

    private void getAndToggle(Consumer<Integer> stateConsumer) {
        Tile tile = getQsTile();
        int state = tile.getState();

        stateConsumer.accept(state);

        tile.setState(
                (state == Tile.STATE_ACTIVE) ? Tile.STATE_INACTIVE: Tile.STATE_ACTIVE);
        tile.updateTile();
        showState(tile.getState());
    }

    private void unlockAndRun() {
        Log.d(TAG, "Unlock and run!");
        unlockAndRun(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Message", "Unlocked!");
            startActivity(intent);
        });
    }

    @SuppressLint("SetTextI18n")
    private void showState(int state) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.my_dialog);
        dialog.setTitle("State Changed");
        dialog.findViewById(R.id.btn_confirm).setOnClickListener((v) -> dialog.dismiss());
        ((TextView)dialog.findViewById(R.id.tv_dialog_message))
                .setText("Feature turned " + ((state == Tile.STATE_ACTIVE) ? "on" : "off"));
        showDialog(dialog);
    }
}