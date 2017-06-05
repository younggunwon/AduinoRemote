package younggun.aduinoremote.model;

/**
 * Created by younggun on 2017-06-05.
 */

public class SettingData {
    private int img;
    private String output;

    public SettingData(int $img, String $output) {
        img = $img;
        output = $output;
    }

    public int getImg() {
        return img;
    }

    public String getOutput() {
        return output;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
