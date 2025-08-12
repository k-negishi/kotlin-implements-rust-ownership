# Kotlin Implementation of Rust's Ownership System and Borrow Checker

<table>
    <thead>
        <tr>
           <th style="text-align:center"><a href="#日本語版">日本語版</a></th>
           <th style="text-align:center"><a href="#english-version">English Version</a></th>     
        </tr>
    </thead>
</table>

---

## 日本語版

### 概要

RustのOwnership（所有権）システムとBorrow Checker（借用チェッカー）をKotlinで実装したプロジェクトです。Rustのメモリ安全性の概念を他の言語で理解・学習するためのデモンストレーションとして作成されています。

**[Kotlin Playground で実行](https://play.kotlinlang.org/embed?short=d6p3rUlDn)**

### 実装機能

- Move semantics（ムーブセマンティクス）
- Ownership validation（所有権検証）
- Borrowing（借用）システム
- Borrow checker（借用チェッカー）

### 主要クラス

- `Owned<T>`: 基本的な所有権コンテナ
- `OwnedWithBorrowChecker<T>`: 借用チェッカー機能付きコンテナ
- `Ref<T>`, `MutRef<T>`: 不変・可変借用

### 実行例

```kotlin
// 所有権の移動
val s1 = Owned("hello")
val s2 = s1.moveTo() // s1は無効化

// 借用チェッカー
val s = OwnedWithBorrowChecker("hello")
val r1 = s.borrow() // 不変借用
val r2 = s.borrow() // 複数の不変借用は可能
// val r3 = s.borrowMut() // エラー: 不変借用中は可変借用不可
```

---

## English Version

### Overview

A Kotlin implementation of Rust's Ownership system and Borrow Checker. This project demonstrates Rust's memory safety concepts in other programming languages for learning purposes.

**[Run in Kotlin Playground](https://play.kotlinlang.org/embed?short=d6p3rUlDn)**

### Features

- Move semantics
- Ownership validation
- Borrowing system
- Borrow checker

### Key Classes

- `Owned<T>`: Basic ownership container
- `OwnedWithBorrowChecker<T>`: Container with borrow checker
- `Ref<T>`, `MutRef<T>`: Immutable and mutable borrows

### Usage Example

```kotlin
// Ownership move
val s1 = Owned("hello")
val s2 = s1.moveTo() // s1 becomes invalid

// Borrow checker
val s = OwnedWithBorrowChecker("hello")
val r1 = s.borrow() // immutable borrow
val r2 = s.borrow() // multiple immutable borrows allowed
// val r3 = s.borrowMut() // Error: cannot create mutable borrow
```