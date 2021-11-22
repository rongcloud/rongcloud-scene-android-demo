package com.basis;

import com.basis.net.oklib.api.ORequest;
import com.basis.ui.IBasis;
import com.kit.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 自定维护界okhttp 发出的请求
 */
public class CallHelper {
    private final static CallHelper helper = new CallHelper();
    private Map<IBasis, List<ORequest>> requestMap = new HashMap<>(8);

    public static CallHelper getHelper() {
        return helper;
    }

    public void applyRequest(ORequest request) {
        IBasis iBasis = UIStack.getInstance().getLastBasis();
        if (null == iBasis) {
            Logger.e("applyRequest:ibasis is null");
            return;
        }
        List<ORequest> requests = requestMap.get(iBasis);
        if (null == requests) {
            requests = new ArrayList<>(4);
        }
        if (!requests.contains(request)) {
            requests.add(request);
        }
    }

    public void removeRequest(IBasis iBasis, ORequest request) {
        if (null == iBasis) {
            Logger.e("removeRequest:ibasis is null");
            return;
        }
        List<ORequest> requests = requestMap.get(iBasis);
        if (null != requests) {
            requests.remove(request);
        }
    }

    public void clear(IBasis iBasis) {
        if (null == iBasis) {
            Logger.e("clear:ibasis is null");
            return;
        }
        List<ORequest> requests = requestMap.get(iBasis);
        int size = null == requests ? 0 : requests.size();
        for (int i = 0; i < size; i++) {
            ORequest request = requests.get(i);
            request.cancel();
        }
        if (requests != null)
            requests.clear();
    }

    public void clearAll() {
        if (null != requestMap && !requestMap.isEmpty()) {
            Set<IBasis> basisSet = requestMap.keySet();
            if (null != basisSet) {
                for (IBasis basis : basisSet) {
                    clear(basis);
                }
            }
        }
    }
}
