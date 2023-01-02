# Art_Adda
**Art Adda is a Kid's Drawing App made using Kotlin with a Custom Drawing View implementation.**

## App Screenshot
<p float="left">
  <img src="https://i.postimg.cc/NjWdprLD/1.jpg" width="150" alt="Art Adda filter">
  <img src="https://i.postimg.cc/rwW1yZFG/2.jpg" width="150" alt="Art Adda filter">
  <img src="https://i.postimg.cc/dQxr2chm/3.jpg" width="150" alt="Art Adda filter">
  <img src="https://i.postimg.cc/NM02XdYz/4.jpg" width="150" alt="Art Adda filter">
  <img src="https://i.postimg.cc/QdkFn2dz/5.jpg" width="150" alt="Art Adda filter">
  <img src="https://i.postimg.cc/J7GpJLrM/6.jpg" width="150" alt="Art Adda filter">
</p>

## Implemented Feature
  - It implements Custom `DrawingView` using the inbuild java class `View`.
    1. Using this DrawingView the user can make Sketches on a white canvas.
    2. This class contains `setColor()` function that can be used by the user at runtime to change the color of the brush.
    3. Another funtion `setBrushThickness()` in the DrawingView class can be used to change the brush thickness.
    4. The `onClickUndo()` and `onClickRedo()` functions from this class can be used at runtime to erase or unerase some path drawn on the canvas.
  - This DrawingView can be implemented in the activity_main.xml file that acts like a canvas to the user.
  - At the bottom of the canvas it has a `HorizontalScrollView` that contains several `ImageButtons`, such as-
    1. Undo/redo botton that can be used for erasing or unerasing paths drawn on the canvas by the user.
    2. Brush thickness selector, when clicked it pop-ups a custom dialog containing three different sizes for brush thickness
    3. Brush color selector, using this button the user can select the required brush color among fifteen different colors through a custom dialog.
    4. Background image selector
    5.
    6.
    
## Download APK
  [ArtAdda.apk](https://drive.google.com/file/d/1pVKrgN1KeehTlQ1FEsCJcr78uMw9e0m3/view?usp=sharing)
