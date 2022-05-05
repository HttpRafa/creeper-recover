package net.rafael.plugins.creeper.recover.update;

//------------------------------
//
// This class was developed by Rafael K.
// On 2/25/2022 at 1:47 PM
// In the project CreeperRecover
//
//------------------------------

public class PluginVersion {

    private int major = 1;
    private int minor = 0;
    private int patch = 0;

    public PluginVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public PluginVersion() {

    }

    public PluginVersion from(String version) {
        String[] numbers = version.split("\\.");
        this.major = Integer.parseInt(numbers[0]);
        this.minor = Integer.parseInt(numbers[1]);
        this.patch = Integer.parseInt(numbers[2]);
        return this;
    }

    public int compare(PluginVersion pluginVersion) {
        return Integer.compare(pluginVersion.asInt(), this.asInt());
    }

    public int asInt() {
        int value = this.major;
        value = (value << 8) + this.minor;
        value = (value << 8) + this.patch;
        return value;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getPatch() {
        return patch;
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

}
