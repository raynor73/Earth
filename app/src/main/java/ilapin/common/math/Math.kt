package ilapin.common.math

fun lerp(a: Float, b: Float, amount: Float): Float {
    return a + (b - a) * amount
}

fun inverseLerp(min: Float, max: Float, value: Float): Float {
    return (value - min) / (max - min)
}