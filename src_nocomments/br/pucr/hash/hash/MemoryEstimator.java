package br.pucr.hash;

public final class MemoryEstimator {
    
    public static final int OBJ_HEADER = 16;     
    public static final int REF_SIZE   = 8;      
    public static final int INT_SIZE   = 4;
    public static final int NODE_OVERHEAD = OBJ_HEADER + INT_SIZE + REF_SIZE; 
    public static final int ARRAY_HEADER = 16;
    public static final int BOOL_SIZE   = 1;

    public static long estimateChainingBytes(int m, long totalNodes) {
        long bucketsRefs = ARRAY_HEADER + (long)m * REF_SIZE;
        long nodes = totalNodes * NODE_OVERHEAD;
        return bucketsRefs + nodes;
    }

    public static long estimateOpenAddressingBytes(int m) {
        
        long arr = ARRAY_HEADER + (long)m * INT_SIZE;
        return arr;
    }

    public static long measureHeapUsedBytes() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }
}
