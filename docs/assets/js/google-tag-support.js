// Support for DPE University follow ups
document.addEventListener("DOMContentLoaded", (event) => {
    var links = document.getElementsByTagName("a");
    for (let i = 0; i < links.length; i++) { 
      var link = links[i]
      if(link.getAttribute("href")?.startsWith("https://dpeuniversity.gradle.com/")) { 
        link.setAttribute("promo", "yes")
        link.setAttribute("promo_offer", "DPEU")
        link.setAttribute("promo_type", link.getElementsByTagName("a") ? "link" : "image")
        if (!link.getAttribute("promo_location")) {
          link.setAttribute("promo_location", "declarative-gradle-site")
        }
      }
    }
});
