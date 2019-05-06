package axp.tool.apkextractor;

import java.util.ArrayList;

public class PermItem {
    private String name;
    private String mod_name;
    private ArrayList<String> permList = new ArrayList<>();

    public PermItem(String name, String permissions) {
        this.name = name;
        if(permissions.equals("") == false){
            addToList(permissions);
        }
    }

    public String getName(){
        return this.name;
    }

    public String getModName(){
        this.mod_name = name.replaceAll("_"," ");
        this.mod_name = this.mod_name.toLowerCase();
        String [] tmp_arr = this.mod_name.split(" ");
        mod_name = "";
        for(int i=0; i<tmp_arr.length; i++){
            tmp_arr[i] = tmp_arr[i].substring(0,1).toUpperCase() + tmp_arr[i].substring(1);
            this.mod_name += (tmp_arr[i] + " ");
        }
        return mod_name;
    }

    public ArrayList<String> getPermList() {
        return this.permList;
    }

    public void addToList(String permissions){
        String [] tmp_arr = permissions.split(" , ");
        for(int i = 0; i < tmp_arr.length; i++){
            permList.add(tmp_arr[i]);
        }
    }
}
