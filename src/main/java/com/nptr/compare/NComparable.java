package com.nptr.compare;

public interface NComparable<T> {
    default int compareTo() {
        return 1;
    }
}
