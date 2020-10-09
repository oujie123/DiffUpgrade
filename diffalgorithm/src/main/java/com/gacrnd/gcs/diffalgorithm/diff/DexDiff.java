package com.gacrnd.gcs.diffalgorithm.diff;

import com.gacrnd.gcs.diffalgorithm.PatchOperation;
import com.gacrnd.gcs.diffalgorithm.android.Dex;
import com.gacrnd.gcs.diffalgorithm.android.StringIdItem;
import com.gacrnd.gcs.diffalgorithm.util.BufferUtil;
import com.gacrnd.gcs.diffalgorithm.util.DexDataBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DexDiff {

    private final Dex oldDex;
    private final Dex newDex;

    public DexDiff(File oldFile, File newFile) throws IOException {
        oldDex = new Dex(oldFile);
        newDex = new Dex(newFile);
    }

    public void diff(File file) throws IOException {
        int oldIndex = 0;
        int newIndex = 0;
        int oldStrCount = oldDex.string_ids.size();
        int newStrCount = newDex.string_ids.size();
        List<PatchOperation> patchOperationList = new ArrayList<>();
        while (oldIndex < oldStrCount || newIndex < newStrCount) {
            //old下标记超过old数据元素个数了
            if (oldIndex >= oldStrCount) {
                //表示new还有，则全是新的
                patchOperationList.add(new PatchOperation(PatchOperation.OP_ADD, newIndex, newDex.string_ids.get(newIndex)));
                newIndex++;
            } else if (newIndex >= newStrCount) {
                // old需要remove
                patchOperationList.add(new PatchOperation(PatchOperation.OP_DEL, oldIndex, oldDex.string_ids.get(oldIndex)));
                oldIndex++;
            } else {
                StringIdItem newItem = newDex.string_ids.get(newIndex);
                StringIdItem oldItem = oldDex.string_ids.get(oldIndex);
                //比较StringIdItem对象内部实现位：比较字符串数据
                int cmpRes = oldItem.compareTo(newItem);
                if (cmpRes < 0) {
                    // old：a new：b 此时应该是删除old的a，new的b继续比较old后续的字符串
                    patchOperationList.add(new PatchOperation(PatchOperation.OP_DEL, oldIndex, oldDex.string_ids.get(oldIndex)));
                    oldIndex++;
                } else if (cmpRes > 0) {//new在前面
                    // old：b new：a 此时应该是增加new的a，old的b继续对比new后续的字符串
                    patchOperationList.add(new PatchOperation(PatchOperation.OP_ADD, newIndex, newDex.string_ids.get(newIndex)));
                    newIndex++;
                } else {
                    oldIndex++;
                    newIndex++;
                }
            }
        }


        Iterator<PatchOperation> patchOperationIt = patchOperationList.iterator();
        PatchOperation prevPatchOperation = null;
        while (patchOperationIt.hasNext()) {
            PatchOperation patchOperation = patchOperationIt.next();
            //如果上一个操作是del 而这次操作又是add
            if (prevPatchOperation != null
                    && prevPatchOperation.op == PatchOperation.OP_DEL
                    && patchOperation.op == PatchOperation.OP_ADD) {
                if (prevPatchOperation.index == patchOperation.index) {
                    prevPatchOperation.op = PatchOperation.OP_REPLACE;
                    prevPatchOperation.item = patchOperation.item;
                    patchOperationIt.remove();
                    prevPatchOperation = null;
                } else {
                    prevPatchOperation = patchOperation;
                }
            } else {
                prevPatchOperation = patchOperation;
            }
        }

        DexDataBuffer dexDataBuffer = new DexDataBuffer();
        FileOutputStream fos = new FileOutputStream(file);
        for (PatchOperation patchOperation : patchOperationList) {
            dexDataBuffer.writeInt(patchOperation.op);
            dexDataBuffer.writeInt(patchOperation.index);
            BufferUtil.writeUnsignedLeb128(dexDataBuffer, patchOperation.item.utf16_size);
            dexDataBuffer.write(BufferUtil.writeMutf8(patchOperation.item.data));
            //需要一个 \0
            dexDataBuffer.writeByte(0);
        }
        fos.write(dexDataBuffer.array());
        fos.close();
    }


}
