package com.example.genderpickercanvas.genderPickerScreen.component


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.genderpickercanvas.R

@Composable
fun GenderPicker(
    modifier: Modifier = Modifier,
    maleGradiant: List<Color> = listOf(Color(0xFF6D6DFF), Color.Blue),
    femaleGradiant: List<Color> = listOf(Color(0xFFEA76FF), Color.Magenta),
    distanceBetweenGenders: Dp = 50.dp,
    pathScaleFactor: Float = 7f,
    onGenderClick: (Gender) -> Unit
) {

    var selectedGender by remember {
        mutableStateOf<Gender>(Gender.Male)
    }
    var center by remember {
        mutableStateOf(Offset.Unspecified)
    }

    val malePathString = stringResource(id = R.string.male_path)
    val femalePathString = stringResource(id = R.string.female_path)

    val malePath = remember {
        PathParser().parsePathString(malePathString).toPath()
    }
    val femalePath = remember {
        PathParser().parsePathString(femalePathString).toPath()
    }

    val malePathBounds = remember {
        malePath.getBounds()
    }
    val femalePathBounds = remember {
        femalePath.getBounds()
    }

    var maleTranslationOffset by  remember {
        mutableStateOf(Offset.Zero)
    }
    var femaleTranslationOffset by  remember {
        mutableStateOf(Offset.Zero)
    }
    var currentSelectOffset by remember {
        mutableStateOf(Offset.Zero)
    }

    val maleSelectionRadius = animateFloatAsState(
        targetValue = if (selectedGender == Gender.Male) 80f else 0f,
        animationSpec = tween(500),
        label = ""
    )
    val femaleSelectionRadius = animateFloatAsState(
        targetValue = if (selectedGender == Gender.FeMale) 80f else 0f,
        animationSpec = tween(500),
        label = ""
    )

    Canvas(
        modifier = modifier
            .pointerInput(true){
                detectTapGestures {
                    val transformMaleRect = Rect(
                        offset = maleTranslationOffset,
                        size = malePathBounds.size * pathScaleFactor
                    )
                    val transformFemaleRect = Rect(
                        offset = femaleTranslationOffset,
                        size = femalePathBounds.size * pathScaleFactor
                    )

                    if (selectedGender !is Gender.Male && transformMaleRect.contains(it)){
                        currentSelectOffset = it
                        selectedGender = Gender.Male
                        onGenderClick(Gender.Male)
                    }else if (selectedGender !is Gender.FeMale && transformFemaleRect.contains(it)){
                        currentSelectOffset = it
                        selectedGender = Gender.FeMale
                        onGenderClick(Gender.FeMale)
                    }

                }
            }
    ){
        center = this.center

        maleTranslationOffset = Offset(
            x = center.x - malePathBounds.width * pathScaleFactor - distanceBetweenGenders.toPx() / 2f,
            y = center.y - pathScaleFactor * malePathBounds.height / 2f
        )
        femaleTranslationOffset = Offset(
            x = center.x + distanceBetweenGenders.toPx() / 2f,
            y = center.y - pathScaleFactor * femalePathBounds.height / 2f
        )

        val unTransformMaleOffset = if(currentSelectOffset == Offset.Zero){
            malePathBounds.center
        }else{
            (currentSelectOffset - maleTranslationOffset) / pathScaleFactor
        }
        val unTransformFeMaleOffset = if(currentSelectOffset == Offset.Zero){
           femalePathBounds.center
        }else{
            (currentSelectOffset - femaleTranslationOffset) / pathScaleFactor
        }

        translate(
            left = maleTranslationOffset.x,
            top = maleTranslationOffset.y
        ) {
            scale(
                scale = pathScaleFactor,
                pivot = malePathBounds.topLeft
            ){
                drawPath(
                    path = malePath,
                    color = Color.LightGray
                )
                clipPath(
                    path = malePath
                ){
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = maleGradiant,
                            center = unTransformMaleOffset,
                            radius = maleSelectionRadius.value + 1f
                        ),
                        center = unTransformMaleOffset,
                        radius = maleSelectionRadius.value
                    )
                }
            }

        }


        translate(
            left = femaleTranslationOffset.x,
            top = femaleTranslationOffset.y
        ) {
            scale(
                scale = pathScaleFactor,
                pivot = femalePathBounds.topLeft
            ){
                drawPath(
                    path = femalePath,
                    color = Color.LightGray
                )
                clipPath(
                    path = femalePath
                ){
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = femaleGradiant,
                            center = unTransformFeMaleOffset,
                            radius = femaleSelectionRadius.value + 1
                        ),
                        center = unTransformFeMaleOffset,
                        radius = femaleSelectionRadius.value
                    )
                }
            }

        }


    }

}