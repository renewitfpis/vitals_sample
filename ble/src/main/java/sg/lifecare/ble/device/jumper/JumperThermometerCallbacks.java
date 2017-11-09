package sg.lifecare.ble.device.jumper;

public interface JumperThermometerCallbacks extends JumperManagerCallbacks {

    void onTemperatureRead(double temperature);
}
