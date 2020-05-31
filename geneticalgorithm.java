import java.util.Random;

// 遺伝子群をクラスタとして扱う
class GenGroup{

    // フィールド(直接触りたくないのでprivateでカプセル化しておく)
    private int[][][] population;  // 遺伝子群の配列
    private int[][][] children; // 子遺伝子群の配列

    // コンストラクタ
    // インスタンス作成に合わせて初期個体群を生成する
    GenGroup(int individual_length, int gene_length, int element_max){

        this.population = new int[individual_length][2][];  // 初期個体配列初期化（各個体）
        Random rdn = new Random();  // ランダムインスタンス生成

        for(int i=0;i<this.population.length;i++){
            this.population[i][1] = new int[gene_length];  // 初期個体配列初期化（遺伝子格納部分）
            for(int j=0;j<this.population[i][1].length;j++){
                this.population[i][1][j] = rdn.nextInt(element_max+1);
            }
        }
        evaluate(population);  // 各遺伝子の評価値を算出

        // デバッグ
        System.out.println("---初期個体遺伝子群を表示---");
        showGenGroup(population);  // 初期個体群を表示
    }

    // 現世代から次世代の生成
    void generateNextGen(int element_max, double pm){
        // 子遺伝子群（2N個）の生成
        generateChildren(element_max, pm);
        
        //  選択
        select();
        // デバッグ（選択によって生成された次世代遺伝子群を表示）
        System.out.println("---次世代遺伝子郡を表示---");
        showGenGroup(this.population);
    }

    // 現世代の最適解を表示するメソッド
    void showOptimalValue(){
        showGen(this.population[0]);
    }

    // privateメソッド(privateでカプセル化)
    // 子を生成するメソッド
    private void generateChildren(int element_max, double pm){

        this.children = new int[this.population.length*2][2][];  // 子遺伝子群の初期化

        // 交叉
        cross();

        // デバッグ（childrenの評価値と遺伝子を表示）
        // System.out.println("---（交叉実施後）childlen---");
        // showGenGroup(this.children);

        // 突然変異
        mutation(element_max, pm);

        // デバッグ（突然変異実施後）
        // System.out.println("---（突然変異実施後）childlen---");
        // showGenGroup(this.children);

        // 子の評価値算出
        evaluate(this.children);

        // デバッグ（childrenの評価値と遺伝子を表示）
        // System.out.println("---（最終型）childlen---");
        // showGenGroup(this.children);
    }

    // 交叉
    // 2点交叉を行い子を生成するメソッド
    private void cross(){            
        int[] child1;  // 1つ目の子遺伝子配列定義
        int[] child2;  // 2つ目の子遺伝子配列定義
        int[] child_dummy;  // 交叉用ダミー子遺伝子配列定義
        
        int r1=0;  // 1点目の交叉ポイント変数定義
        int r2=0;  // 2点目の交叉ポイント変数定義
        int target = 0;  // 交叉対象変数定義
        Random rdn = new Random();  // ランダムインスタンス生成

        for(int i=0; i<this.population.length; i++){
            this.children[2*i][0] = new int[1];  // 子遺伝子群の初期化（評価値格納部分）
            this.children[2*i+1][0] = new int[1];  // 子遺伝子群の初期化（評価値格納部分）
            this.children[2*i][1] = new int[this.population[i][1].length];  // 子遺伝子群の初期化（遺伝子格納配列）
            this.children[2*i+1][1] = new int[this.population[i][1].length];  // 子遺伝子群の初期化（遺伝子格納配列）


            child1 = new int[this.population[i][1].length];  // 1つ目の子遺伝子配列初期化
            child2 = new int[this.population[i][1].length];  // 2つ目の子遺伝子配列初期化
            child_dummy = new int[this.population[i][1].length];  // 交叉用ダミー子遺伝子配列初期化

            r1 = rdn.nextInt(this.population[i][1].length-1);  // 1点目の交叉ポイント決定（0<= r1 < length-1）
            r2 = rdn.nextInt(this.population[i][1].length-r1-1)+(r1+1); // 2点目の交叉ポイント決定（r1 < r2 < length）
            target = rdn.nextInt(this.population.length); //交叉対象を決定
            
            // 子遺伝子配列に現世代遺伝子をコピー
            for(int j=0; j < this.population[i][1].length;j++){
                child1[j] = this.population[i][1][j];
                child_dummy[j] = this.population[i][1][j];
                child2[j] = this.population[target][1][j];
            }

            // 2点交叉実行
            for(int j=r1; j <= r2; j++){
                child1[j] = child2[j];
                child2[j] = child_dummy[j];
            }

            // children配列に交叉で生成した子遺伝子と、その評価値を格納
            for(int j=0; j < this.population[i][1].length; j++){
                this.children[2*i][1][j]=child1[j];                
                this.children[2*i+1][1][j]=child2[j];
            }
        }
    }

    // 突然変異のメソッド
    private void mutation(int element_max, double pm){
        // children配列に遺伝子が存在するかを判定（未実装）
        Random rdn = new Random();  // ランダムインスタンス生成

        for(int i=0; i<this.children.length; i++){
            for(int j=0; j<this.children[i][1].length; j++){
                if(pm>=rdn.nextInt(101)){  // 突然変異率を満たしたら遺伝子要素変更
                    this.children[i][1][j] = rdn.nextInt(element_max+1);
                }
            }
        }
    }

    // 選択のメソッド
    // エリート選択
    // childrenの2M個の遺伝子郡から評価値が高い個体をM個選出する
    private void select(){
        for(int i=0; i<this.population.length; i++){
            this.population[i] = copyArray(this.children[i]);
        }
    }

    // 評価関数
    private int evaluateFunction(int[] gen){
        int val =0;
        for(int i=0; i<gen.length; i++){
            val += gen[i];
        }
        return val;
    }

    // 評価関数を使って各遺伝子の評価値を算出し、大きい順に並び替えるメソッド
    private void evaluate(int[][][] array){
        for(int i=0;i<array.length;i++){
            array[i][0] = new int[1];  // 初期個体配列初期化（評価値格納部分）
            array[i][0][0] = evaluateFunction(array[i][1]);
        }
        // 評価値の高い順に並び替える
        for(int i=0; i<array.length; i++){
            for(int j=0; j<array.length; j++){
                if(array[j][0][0]<array[i][0][0]){
                    int[][] temp = array[i];
                    array[i]=copyArray(array[j]);
                    array[j]=copyArray(temp);
                }
            }
        }
    }

    // {{評価値}, {遺伝子}}を表示する
    private void showGen(int[][] array){
        System.out.printf("%2d ",array[0][0]);
        for(int i=0; i<array[1].length; i++){
            System.out.print(array[1][i]);
        }
        System.out.println();
    }

    // 遺伝子群（population,children）の中身を表示するメソッド
    private void showGenGroup(int[][][] array){
        for(int i=0; i<array.length; i++){
            // 評価値
            System.out.printf("%2d ",array[i][0][0]);
            // 遺伝子
            for(int j=0; j < array[i][1].length;j++){
                    System.out.print(array[i][1][j]);               
            }
            System.out.println();
        }
    }

    // 配列コピーメソッド（オーバーロードで各次元に対応するようにする）
    private int[] copyArray(int[] src){
        int[] dst = new int[src.length];
        for(int i=0; i < src.length; i++){
            dst[i] = src[i];
        }
        return dst;
    }
    private int[][] copyArray(int[][] src){
        int[][] dst = new int[src.length][];
        for(int i=0; i < src.length; i++){
            dst[i] = new int[src[i].length];
            for(int j=0; j < src[i].length; j++){
                dst[i][j] = src[i][j];
            }
        }
        return dst;
    }
    private int[][][] copyArray(int[][][] src){
        int[][][] dst = new int[src.length][][];
        for(int i=0; i < src.length; i++){
            dst[i] = new int[src[i].length][];
            for(int j=0; j < src[i].length; j++){
                dst[i][j] = new int[src[i][j].length];
                for(int k=0; k < src[i][j].length; k++){
                    dst[i][j][k] = src[i][j][k];
                }
            }
        }
        return dst;
    }
}

class GeneticAlgorithm{
    // メインメソッド
    public static void main(String[] args) {

        // 処理前の時刻を取得
        long startTime = System.currentTimeMillis();

        int individual_length =10;  // 個体数
        int gene_length = 9;  // 遺伝子要素数
        int element_max = 9;  // 遺伝子要素の最大値
        double pm = 5;  //突然変異率（%）
        int N = 50; //試行回数

        GenGroup population1 = new GenGroup(individual_length, gene_length, element_max);  // 遺伝子群インスタンスをを生成する

        // 試行回数分ループ
        for(int i=1; i<=N; i++){
            // 次世代個体を生成
            System.out.println("★★★" + i + "世代★★★");
            population1.generateNextGen(element_max, pm);
            System.out.println("");
        }

        // 最適解の表示
        System.out.print("最適解=");
        population1.showOptimalValue();

        // 処理後の時刻を取得
        long endTime = System.currentTimeMillis();
    
        System.out.println("＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊");
        System.out.println("開始時刻：" + startTime + " ms");
        System.out.println("終了時刻：" + endTime + " ms");
        System.out.println("処理時間：" + (endTime - startTime) + " ms");

    }
}