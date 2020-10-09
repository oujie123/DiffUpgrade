package com.gacrnd.gcs.diffalgorithm;

import com.gacrnd.gcs.diffalgorithm.diff.DexDiff;
import com.gacrnd.gcs.diffalgorithm.diff.DexPatch;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        DexDiff dexDiff = new DexDiff(new File("G:\\android_project\\DiffUpgrade\\diffalgorithm\\src\\main\\java\\old.dex"),
                new File("G:\\android_project\\DiffUpgrade\\diffalgorithm\\src\\main\\java\\new.dex"));
        dexDiff.diff(new File("G:\\android_project\\DiffUpgrade\\diffalgorithm\\src\\main\\java\\patch.dex"));

        DexPatch dexPatch = new DexPatch(new File("G:\\android_project\\DiffUpgrade\\diffalgorithm\\src\\main\\java\\old.dex"),
                new File("G:\\android_project\\DiffUpgrade\\diffalgorithm\\src\\main\\java\\patch.dex"));
        dexPatch.patch(new File("G:\\android_project\\DiffUpgrade\\diffalgorithm\\src\\main\\java\\new2.dex"));

    }
}
