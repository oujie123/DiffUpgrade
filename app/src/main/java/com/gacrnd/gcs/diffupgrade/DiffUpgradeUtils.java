package com.gacrnd.gcs.diffupgrade;

/**
 * @author Jack_Ou  created on 2020/10/9.
 */
public class DiffUpgradeUtils {

    public static native int patch(String oldApk,String newApk,String patch);
}
