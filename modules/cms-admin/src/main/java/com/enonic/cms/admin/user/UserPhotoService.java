package com.enonic.cms.admin.user;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.portal.image.filter.effect.ScaleSquareFilter;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public final class UserPhotoService
{
    private final BufferedImage defaultImage;

    public UserPhotoService()
        throws Exception
    {
        this.defaultImage = ImageIO.read(getClass().getResourceAsStream("default_user.gif"));
    }

    public byte[] renderPhoto(final UserEntity user, final int size)
        throws Exception
    {
        final BufferedImage image = createImage(user.getPhoto());
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
