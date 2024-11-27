<!-- Wistia Embedded Videos -->
<script src="https://fast.wistia.net/assets/external/E-v1.js" async></script>

<!-- omit in toc -->
# Features

!!! tip
    Check out the features below with one of our [samples](./samples.md). These features are guaranteed to work with them out of the box.

[Provide us feedback](../feedback.md).

## Declarative DSL in IDE

### Android Studio

<iframe width="709" height="400" src="https://www.youtube.com/embed/POjnWOwWqco" title="First Look at Declarative Gradle - Android Studio" frameborder="0" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>

This video demonstrates the support for the Declarative Configuration Language (DCL) available in [Android Studio](./setup.md#android-studio): syntax highlighting, semantic analysis and code completion.
Thanks to the strictness of DCL, the IDE assistance is exempt of noise.
Code completion only suggests the properties and nested blocks available in the current scope.

<script src="https://fast.wistia.com/embed/medias/7sgdqj7lcn.jsonp" async></script>
<div class="wistia_responsive_padding" style="padding:56.25% 0 0 0;position:relative;">
    <div class="wistia_responsive_wrapper" style="height:100%;left:0;position:absolute;top:0;width:100%;">
        <div class="wistia_embed wistia_async_7sgdqj7lcn seo=true videoFoam=true" style="height:100%;position:relative;width:100%">
            <div class="wistia_swatch" style="height:100%;left:0;opacity:0;overflow:hidden;position:absolute;top:0;transition:opacity 200ms;width:100%;">
                <img src="https://fast.wistia.com/embed/medias/7sgdqj7lcn/swatch" style="filter:blur(5px);height:100%;object-fit:contain;width:100%;" alt="" aria-hidden="true" onload="this.parentNode.style.opacity=1;" />
            </div>
        </div>
    </div>
</div>

This video demonstrates the enhanced support for DCL in Android Studio, covering enum properties and named domain object containers.

The same level of support should be expected in [IntelliJ IDEA](./setup.md#intellij-idea).

### Visual Studio Code

<script src="https://fast.wistia.com/embed/medias/8t8appyr68.jsonp" async></script>
<div class="wistia_responsive_padding" style="padding:56.25% 0 0 0;position:relative;">
    <div class="wistia_responsive_wrapper" style="height:100%;left:0;position:absolute;top:0;width:100%;">
        <div class="wistia_embed wistia_async_8t8appyr68 seo=true videoFoam=true" style="height:100%;position:relative;width:100%">
            <div class="wistia_swatch" style="height:100%;left:0;opacity:0;overflow:hidden;position:absolute;top:0;transition:opacity 200ms;width:100%;">
                <img src="https://fast.wistia.com/embed/medias/8t8appyr68/swatch" style="filter:blur(5px);height:100%;object-fit:contain;width:100%;" alt="" aria-hidden="true" onload="this.parentNode.style.opacity=1;" />
            </div>
        </div>
    </div>
</div>

This video demonstrates the support for the Declarative Configuration Language (DCL) available in [Visual Studio Code](./setup.md#visual-studio-code): syntax highlighting, semantic analysis and code completion.
Thanks to the strictness of DCL, the IDE assistance is exempt of noise.
Code completion only suggests the properties and nested blocks available in the current scope.
Moreover, semantic [mutations](#mutations), or refactorings, are available directly in the editor.

### Eclipse IDE

<script src="https://fast.wistia.com/embed/medias/mosuja84ou.jsonp" async></script>
<div class="wistia_responsive_padding" style="padding:56.25% 0 0 0;position:relative;">
    <div class="wistia_responsive_wrapper" style="height:100%;left:0;position:absolute;top:0;width:100%;">
        <div class="wistia_embed wistia_async_mosuja84ou seo=true videoFoam=true" style="height:100%;position:relative;width:100%">
            <div class="wistia_swatch" style="height:100%;left:0;opacity:0;overflow:hidden;position:absolute;top:0;transition:opacity 200ms;width:100%;">
                <img src="https://fast.wistia.com/embed/medias/mosuja84ou/swatch" style="filter:blur(5px);height:100%;object-fit:contain;width:100%;" alt="" aria-hidden="true" onload="this.parentNode.style.opacity=1;" />
            </div>
        </div>
    </div>
</div>

This video demonstrates the support for the Declarative Configuration Language (DCL) available in the [Eclipse IDE](./setup.md#eclipse-ide): syntax highlighting, semantic analysis and code completion.
Thanks to the strictness of DCL, the IDE assistance is exempt of noise.
Code completion only suggests the properties and nested blocks available in the current scope.

## Two-Way Tooling

<iframe width="709" height="400" src="https://www.youtube.com/embed/0PBQ2gbQfjU" title="First Look at Declarative Gradle - Two-Way Tooling" frameborder="0" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>

This video shows the [Gradle Client](./setup.md#gradle-client) application and demonstrates the use of the Declarative Configuration Language (DCL) tooling libraries to inspect the model of a configured build.
The configuration of projects is overlayed on top of the defaults declared in settings.
Highlighting configured values works in a two-way manner ; from the DCL files to the configured model and vice-versa.

## Mutations

<iframe width="709" height="400" src="https://www.youtube.com/embed/pYuVFtfMNzM" title="First Look at Declarative Gradle - Mutations" frameborder="0" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>

This video shows the [Gradle Client](./setup.md#gradle-client) application and demonstrates the use of the _mutation_ framework from the Declarative Configuration Language (DCL) tooling libraries.
Available mutations, or refactorings, are shown on the configured model pane where they are applicable.
For example, the _Add a dependency_ mutation is available on the `dependencies {}` block.
When a mutation is triggered, the Gradle Client user interface asks for the parameters of the mutation and the mutation is applied, changing the DCL files.
This demonstration also shows that mutations can be applied on DCL files that contain errors.
