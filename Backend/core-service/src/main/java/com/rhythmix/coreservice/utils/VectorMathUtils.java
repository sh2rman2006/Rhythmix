package com.rhythmix.coreservice.utils;

public class VectorMathUtils {

    public static void normalize(float[] vector) {
        float norm = 0f;
        for (float v : vector) norm += v * v;
        norm = (float) Math.sqrt(norm);
        if (norm > 0f) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
    }

    public static void divide(float[] vector, float scalar) {
        if (scalar == 0f) return;
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= scalar;
        }
    }

    public static float[] average(java.util.List<float[]> vectors, int dim) {
        float[] result = new float[dim];
        for (float[] vec : vectors) {
            for (int i = 0; i < dim; i++) {
                result[i] += vec[i];
            }
        }
        if (!vectors.isEmpty()) divide(result, vectors.size());
        return result;
    }
}
