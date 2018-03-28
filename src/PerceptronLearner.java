import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.ops.MatrixFeatures;
import org.ejml.ops.NormOps;

public class PerceptronLearner implements Learner
{
    private DenseMatrix64F weights;
    public static final int NUM_ROWS = 3;
    public static final int NUM_COLS = 3;

    public PerceptronLearner()
    {
        weights = new DenseMatrix64F(NUM_ROWS, NUM_COLS);
    }

    public PerceptronLearner(double[] array)
    {
        setWeights(array);
    }

    public void setWeights(double[] array)
    {
        weights = new DenseMatrix64F(NUM_ROWS, NUM_COLS, true, array);
        normalizeWeights();
    }

    public double[] getWeights()
    {
        return weights.getData();
    }

    public void randomizeWeights()
    {
        for (int i = 0; i < weights.getNumElements(); i++)
        {
            weights.set(i, Math.random());
        }

        normalizeWeights();
    }

    public void normalizeWeights()
    {
        for (int i = 0; i < weights.getNumRows(); i++)
        {
            DenseMatrix64F temp = extractRow(weights, i, null);
            CommonOps.divide(CommonOps.elementSum(temp), temp);
            CommonOps.insert(temp,weights,i,0);
        }
    }

    /**
     * Extracts the row from a matrix.
     * @param a Input matrix
     * @param row Which row is to be extracted
     * @param out output. Storage for the extracted row. If null then a new vector will be returned.
     * @return The extracted row.
     */
    public static DenseMatrix64F extractRow( DenseMatrix64F a , int row , DenseMatrix64F out ) {
        if( out == null)
            out = new DenseMatrix64F(1,a.numCols);
        else if( !MatrixFeatures.isVector(out) || out.getNumElements() != a.numCols )
            throw new IllegalArgumentException("Output must be a vector of length "+a.numCols);

        System.arraycopy(a.data,a.getIndex(row,0),out.data,0,a.numCols);

        return out;
    }

    @Override
    public CarCommands getCommands(double[] inputs)
    {
        DenseMatrix64F result = new DenseMatrix64F(3, 1);
        CommonOps.multTransAB(weights, new DenseMatrix64F(1, inputs.length, true, inputs), result);
        int maxIndex = 0;
        double max = result.get(maxIndex);

        for (int i = 1; i < result.getNumElements(); i++)
        {
            if (result.get(i) > max)
            {
                max = result.get(i);
                maxIndex = i;
            }
        }

        if (maxIndex == 0)
            return CarCommands.TURN_LEFT;
        if (maxIndex == 2)
            return CarCommands.TURN_RIGHT;
        return CarCommands.GO_STRAIGHT;
    }
}
