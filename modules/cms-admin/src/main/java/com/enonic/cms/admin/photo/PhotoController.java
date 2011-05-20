package com.enonic.cms.admin.photo;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.portal.image.filter.effect.ScaleSquareFilter;
import com.enonic.cms.store.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/photo")
public final class PhotoController
{
    @Autowired
    private UserDao userDao;

    private final BufferedImage defaultImage;

    public PhotoController()
        throws Exception
    {
        this.defaultImage = ImageIO.read(getClass().getResourceAsStream("default_user.gif"));
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<byte[]> getPhoto(@RequestParam("key") final String key, @RequestParam("thumb") final boolean thumb)
        throws Exception
    {
        final byte[] photoBytes = findPhoto(key);
        final BufferedImage photoImage = createImage(photoBytes);
        final BufferedImage scaledImage = scaleImage(photoImage, thumb ? 40 : 100);
        final byte[] scaledBytes = toBytes(scaledImage);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("image/png"));

        return new ResponseEntity<byte[]>(scaledBytes, headers, HttpStatus.OK);
    }

    private byte[] findPhoto(final String key)
    {
        if (key == null) {
            return null;
        }

        final UserEntity entity = this.userDao.findByKey(new UserKey(key));
        if (entity == null) {
            return null;
        }

        return entity.getPhoto();
    }

    private BufferedImage createImage(final byte[] data)
        throws Exception
    {
        if (data == null) {
            return this.defaultImage;
        }

        return toBufferedImage(data);
    }

    private BufferedImage toBufferedImage(final byte[] data)
        throws Exception
    {
        return ImageIO.read(new ByteArrayInputStream(data));
    }
    
    private BufferedImage scaleImage(final BufferedImage image, final int size)
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
