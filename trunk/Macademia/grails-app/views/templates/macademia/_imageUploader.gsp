     %{--Warning: the upload javascript relies on the class and id names below.--}%
     %{--Change them at your own risk--}%
     <div class="editPicture left">
      <div>
        <g:if test="${user?.imageSubpath}">
          <img src="/Macademia/${group}/image/retrieve?subPath=${user.imageSubpath}" alt="" defaultImage="/Macademia/images/scholar_cat.gif"/>
        </g:if>
        <g:else>
          <img src="/Macademia/${group}/images/scholar_cat.gif" width="50"  alt="" defaultImage="/Macademia/images/scholar_cat.gif"/>
        </g:else>
      </div>
      <div class="links">
          %{--These elements must appear in exactly this order for the upload functionality to work--}%
          <a href="#" class="change">change picture</a> <span class="separator">|</span><a href="#" class="delete">delete</a>
          <div id="imgUploader">&nbsp;</div>
      </div>
      <input type="hidden" name="imageSubpath" value="${user?.imageSubpath}"/>
    </div>
