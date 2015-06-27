<form>
<#list reviews as review>
    <input type="text" value="${review.id}">
    <p>${review.text}</p>
    <p>
        1: <input type="radio" name="proScore" value="1">
        2: <input type="radio" name="proScore" value="2">
        3: <input type="radio" name="proScore" value="3">
    </p>
</#list>
</form>