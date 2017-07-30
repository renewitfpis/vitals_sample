package sg.lifecare.ble.device.jumper;

public interface JumperOximeterCallbacks extends JumperManagerCallbacks {

    void onSpo2PulsePiRead(int spo2, int pulse, double pi);
}
