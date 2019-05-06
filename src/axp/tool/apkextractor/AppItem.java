package axp.tool.apkextractor;

public class AppItem {

    String package_name, icon, url;

    public AppItem(String package_name, String icon, String url){
        this.package_name = package_name;
        this.icon = icon;
        this.url = url;
    }

    public String getPackage_name() {
        return package_name;
    }

    public String getIcon() {
        return icon;
    }

    public String getUrl() {
        return url;
    }
}
