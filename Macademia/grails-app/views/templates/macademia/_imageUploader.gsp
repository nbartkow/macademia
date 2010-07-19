     %{--Warning: the upload javascript relies on the class and id names below.--}%
     %{--Change them at your own risk--}%
     <div class="editPicture left">
      <div>
        <g:if test="${user?.profile?.imageSubpath}">
          <img src="/Macademia/images/db/large/${user.profile.imageSubpath}" alt="" defaultImage="/Macademia/images/scholar_cat.gif"/>
        </g:if>
        <g:else>
          <img src="/Macademia/images/scholar_cat.gif" width="50"  alt="" defaultImage="/Macademia/images/scholar_cat.gif"/>
        </g:else>
      </div>
      <div class="links">
          %{--These elements must appear in exactly this order for the upload functionality to work--}%
          <a href="#" class="change">change picture</a> <span class="separator">|</span><a href="#" class="delete">delete</a>
          <div id="imgUploader">&nbsp;</div>
      </div>
      <input type="hidden" name="imageSubpath" value=""/>
    </div>