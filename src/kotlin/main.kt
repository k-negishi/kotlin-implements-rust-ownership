fun main() {
    // 基本的な所有権と借用のデモ
    demonstrateBasicOwnership()

    // 借用チェッカーのデモ
    demonstrateBorrowChecker()

    // もしくは、demonstrateBorrowChecker() だけを実行したい場合は
    // demonstrateBasicOwnership() をコメントアウトしてください
}


// Rust風の所有権クラス
class Owned<T>(private var value: T) {
    private var isValid = true

    fun getValue(): T {
        check(isValid) { "使用済みの値にアクセスしようとしています" }
        return value
    }

    fun moveTo(): Owned<T> {
        check(isValid) { "使用済みの値を移動しようとしています" }
        isValid = false // 元の所有者を無効化
        return Owned(value) // 新しい所有者を作成
    }

    fun borrow(): Ref<T> {
        check(isValid) { "使用済みの値を借用しようとしています" }
        return Ref(value)
    }

    fun borrowMut(): MutRef<T> {
        check(isValid) { "使用済みの値を可変借用しようとしています" }
        return MutRef(value, this::updateValue)
    }

    private fun updateValue(newValue: T) {
        value = newValue
    }
}

// 不変参照
class Ref<T>(private val value: T) {
    fun getValue(): T = value
}

// 可変参照
class MutRef<T>(private var value: T, private val updateCallback: (T) -> Unit) {
    fun getValue(): T = value

    fun setValue(newValue: T) {
        value = newValue
        updateCallback(newValue)
    }
}

// 借用チェッカー付き所有権クラス
class OwnedWithBorrowChecker<T>(private var value: T) {
    private var isValid = true
    private var immutableBorrows = 0
    private var hasMutableBorrow = false

    fun getValue(): T {
        check(isValid) { "使用済みの値にアクセスしようとしています" }
        return value
    }

    fun borrow(): RefWithRelease<T> {
        check(isValid) { "使用済みの値を借用しようとしています" }
        check(!hasMutableBorrow) { "すでに可変借用が存在します" }

        immutableBorrows++
        return RefWithRelease(value, this::releaseImmutableBorrow)
    }

    fun borrowMut(): MutRefWithRelease<T> {
        check(isValid) { "使用済みの値を可変借用しようとしています" }
        check(immutableBorrows == 0) { "不変借用が存在する間は可変借用できません" }
        check(!hasMutableBorrow) { "すでに可変借用が存在します" }

        hasMutableBorrow = true
        return MutRefWithRelease(value, this::updateValue, this::releaseMutableBorrow)
    }

    private fun releaseImmutableBorrow() {
        immutableBorrows--
    }

    private fun releaseMutableBorrow() {
        hasMutableBorrow = false
    }

    private fun updateValue(newValue: T) {
        value = newValue
    }

    fun borrowState(): String {
        return "不変借用数: $immutableBorrows, 可変借用: ${if(hasMutableBorrow) "あり" else "なし"}"
    }
}

// 解放コールバック付き不変参照
class RefWithRelease<T>(private val value: T, private val releaseCallback: () -> Unit) {
    fun getValue(): T = value

    fun release() {
        releaseCallback()
    }
}

// 解放コールバック付き可変参照
class MutRefWithRelease<T>(
    private var value: T,
    private val updateCallback: (T) -> Unit,
    private val releaseCallback: () -> Unit
) {
    fun getValue(): T = value

    fun setValue(newValue: T) {
        value = newValue
        updateCallback(newValue)
    }

    fun release() {
        releaseCallback()
    }
}

// 基本的な所有権と借用のデモ
fun demonstrateBasicOwnership() {
    println("===== 基本的な所有権と移動のテスト =====")
    val s1 = Owned("hello")
    println("s1 の値: ${s1.getValue()}")

    val s2 = s1.moveTo()
    println("s2 の値: ${s2.getValue()}")

    try {
        println("s1 の値: ${s1.getValue()}")
    } catch (e: Exception) {
        println("エラー: ${e.message}")
    }

    println("\n===== 借用のテスト =====")
    val r1 = s2.borrow()
    val r2 = s2.borrow()
    println("r1 の値: ${r1.getValue()}")
    println("r2 の値: ${r2.getValue()}")

    val r3 = s2.borrowMut()
    r3.setValue(r3.getValue() + ", world")
    println("変更後の s2 の値: ${s2.getValue()}")
}

// 借用チェッカーのデモ
fun demonstrateBorrowChecker() {
    println("\n===== 借用チェッカーのテスト =====")
    val s = OwnedWithBorrowChecker("hello")
    println("初期状態: ${s.borrowState()}")

    val r1 = s.borrow()
    val r2 = s.borrow()
    println("2つの不変借用後: ${s.borrowState()}")

    try {
        val r3 = s.borrowMut()
        println("このメッセージは表示されないはず")
    } catch (e: Exception) {
        println("予想通りのエラー: ${e.message}")
    }

    r1.release()
    println("1つ解放後: ${s.borrowState()}")

    r2.release()
    println("すべて解放後: ${s.borrowState()}")

    val r3 = s.borrowMut()
    println("可変借用後: ${s.borrowState()}")

    try {
        val r4 = s.borrow()
        println("このメッセージは表示されないはず")
    } catch (e: Exception) {
        println("予想通りのエラー: ${e.message}")
    }

    r3.setValue("hello, kotlin!")
    println("値変更後: ${s.getValue()}")

    r3.release()
    println("可変借用解放後: ${s.borrowState()}")
}
