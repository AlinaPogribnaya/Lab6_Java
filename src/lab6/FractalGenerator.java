package lab6;
import java.awt.geom.Rectangle2D;

/**
 * Ётот класс предоставл€ет общий интерфейс и операции дл€ генераторов 
 * фракталов, которые можно просматривать в Fractal Explorer.
 */

public abstract class FractalGenerator {
	/**
     * Ёта статическа€ вспомогательна€ функци€ принимает целочисленную координату и 
     * преобразует ее в значение двойной точности, соответствующее определенному диапазону. 
     * ќн используетс€ дл€ преобразовани€ координат пикселей в значени€ 
     * двойной точности дл€ вычислени€ фракталов и т.д.
     *
     * @param rangeMin минимальное значение диапазона с плавающей точкой
     * @param rangeMax максимальное значение диапазона с плавающей точкой
     *
     * @param size размер измерени€, из которого координата пиксел€. 
     *      Ќапример, это может быть ширина изображени€ или высота изображени€.
     *
     * @param coord координата дл€ вычислени€ значени€ двойной точности.
     *       оордината должна находитьс€ в диапазоне [0, размер].
     */
    public static double getCoord(double rangeMin, double rangeMax,
        int size, int coord) 
    {
        assert size > 0;
        assert coord >= 0 && coord < size;

        double range = rangeMax - rangeMin;
        return rangeMin + (range * (double) coord / (double) size);
    }


    /**
     * ”станавливает указанный пр€моугольник,
     * чтобы содержать начальный диапазон, подход€щий дл€ генерируемого фрактала.
     */
    public abstract void getInitialRange(Rectangle2D.Double range);

    /**
     * ќбновл€ет текущий диапазон с центром в указанных координатах, 
     * а также дл€ увеличени€ или уменьшени€ с помощью 
     * указанного коэффициента масштабировани€.
     */
    public void recenterAndZoomRange(Rectangle2D.Double range,
        double centerX, double centerY, double scale) 
    {

        double newWidth = range.width * scale;
        double newHeight = range.height * scale;

        range.x = centerX - newWidth / 2;
        range.y = centerY - newHeight / 2;
        range.width = newWidth;
        range.height = newHeight;
    }

    /**
     * ”читыва€ координату <em> x </ em> + <em> iy </ em> в комплексной плоскости, 
     * она вычисл€ет и возвращает количество итераций, прежде чем фрактальна€ функци€ 
     * покидает ограничивающую область дл€ этой точки. 
     * “очка, котора€ не проходит до достижени€ предела итерации, 
     * указываетс€ с результатом -1.
     */
    public abstract int numIterations(double x, double y);

}
