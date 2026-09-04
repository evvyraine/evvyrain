---
title: Making the web wiggle
date: September 4, 2026
summary: A few notes on building a portfolio that feels playful without getting in the way.
banner: /og-image.jpg
tags: Design, Compose
---

## Motion with a reason

The best interface motion is not decoration pasted on at the end. It tells you **what changed**, keeps your place, and gives every interaction a little personality.

For this site, that meant a few simple rules:

- movement should respond to input;
- shapes should build a composition, not become visual noise;
- the content should remain comfortable on every screen.

> Playful and usable are friends. The interesting work is finding the balance.

## Built a little differently

This portfolio runs on **Kotlin Multiplatform** and Compose for Web. The same declarative ideas I enjoy on Android shape the whole experience here — from navigation to the springy project carousel.

```kotlin
MaterialExpressiveTheme(
    motionScheme = MotionScheme.expressive()
) {
    Portfolio()
}
```

There is always another tiny detail to tune. That is probably the fun part.
