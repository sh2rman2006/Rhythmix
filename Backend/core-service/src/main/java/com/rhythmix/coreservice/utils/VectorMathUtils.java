package com.rhythmix.coreservice.utils;

public class VectorMathUtils {

    private VectorMathUtils() {
    }

    private static final float EPSILON = 1e-8f;

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

    public static float cosineSimilarity(float[] a, float[] b) {
        if (a == null || b == null || a.length != b.length) {
            return -1f; // Некорректные данные
        }

        float dot = 0f;
        float normA = 0f;
        float normB = 0f;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        float denominator = (float) (Math.sqrt(normA) * Math.sqrt(normB)) + EPSILON;

        return Math.max(-1f, Math.min(1f, dot / denominator));
    }

}
