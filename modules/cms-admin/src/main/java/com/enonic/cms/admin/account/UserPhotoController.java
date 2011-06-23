package com.enonic.cms.admin.account;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.portal.image.filter.effect.ScaleSquareFilter;
import com.enonic.cms.store.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Controller
@RequestMapping("/data/photo")
public final class UserPhotoController
{
    @Autowired
    private UserDao userDao;

    private final BufferedImage defaultImage;

    public UserPhotoController()
        throws Exception
    {
        this.defaultImage = ImageIO.read(getClass().getResourceAsStream("default_user.gif"));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> renderPhoto(@RequestParam("key") final String key,
                                          @RequestParam(value = "thumb", defaultValue = "false") final boolean thumb)
        throws Exception
    {
        final UserEntity user = this.userDao.findByKey(key);
        final byte[] bytes = renderPhoto(user, thumb ? 40 : 100);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        
        return new HttpEntity<byte[]>(bytes, headers);
    }

    private byte[] renderPhoto(final UserEntity user, final int size)
        throws Exception
    {
        final BufferedImage image = createImage(user != null ? user.getPhoto() : null);
        final BufferedImage sizedImage = resizeImage(image, size);
        return toBytes(sizedImage);
    }

    private BufferedImage toBufferedImage(final byte[] data)
        throws Exception
    {
        return ImageIO.read(new ByteArrayInputStream(data));
    }

    private BufferedImage createImage(final byte[] data)
        throws Exception
    {
        if (data == null) {
            return this.defaultImage;
        }

        return toBufferedImage(data);
    }

    private BufferedImage resizeImage(final BufferedImage image, final int size)
        throws Exception
    {
        return new ScaleSquareFilter(size).filter(image);
    }

    private byte[] toBytes(final BufferedImage image)
        throws Exception
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        out.close();
        return out.toByteArray();
    }
}
