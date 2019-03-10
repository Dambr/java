public class Main{
	private static double atanh(double x){
		return Math.log( Math.sqrt(1 - Math.pow(x, 2)) / (1 - x) );
	}

	private static double tanh(double x){
		return (Math.exp(2 * (x*Math.pow(2, 1))/Math.pow(2, 1)) - 1) / (Math.exp(2 * (x*Math.pow(2, 1))/Math.pow(2, 1)) + 1);
		//return (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x));
	}


	public static void main (String[] args){
		int N = 10;
		double [] cityx = {0.4, 0.2439, 0.1707, 0.2293, 0.5171, 0.8732, 0.6878, 0.8488, 0.6683, 0.6195, 0.9125};
		double [] cityy = {0.4439, 0.1463, 0.2293, 0.761, 0.9414, 0.6536, 0.5219, 0.3609, 0.2536, 0.2634, 0.9568};
		double [][] d = new double [cityx.length][cityy.length];
		double [][] Udao = new double [cityx.length][cityy.length];
		double [][] V = new double [cityx.length][cityy.length];
		// вычисляем расстояния
		for ( int i = 1; i <= N; i ++ ){
			for ( int j = 1; j <= N; j ++ ){
				d[i][j] = (double)Math.sqrt( Math.pow( (cityx[i] - cityx[j]) , 2) + Math.pow( cityy[i] - cityy[j] ,2) );
			}
		}
		// объявляем константы
		int A = 500;
		int B = 500;
		int C = 1000;
		int D = 500;
		double u0 = 0.02;

		// шаг пересчета
		double lamda = 0.0001f;
		// переменные для основного цикла
		int total = 0;
		int toend = 0;

		long start = System.currentTimeMillis();
		while (toend == 0) {
			//System.out.println((System.currentTimeMillis() - start) / 1000);
			total=total+1;
			// инициализация массива функций передачи нейрона
			//double [][] V = new double [cityx.length][cityy.length];
			for ( int i = 0; i < V.length; i ++ ){
				for ( int j = 0; j < V[i].length; j ++ ){
					V[i][j] = Math.random();
				}
			}

		

			double [][] U = new double [cityx.length][cityy.length];
			for ( int i = 0; i < U.length; i ++ ){
				for ( int j = 0; j < U.length; j ++ ){
					U[i][j] = atanh(2*V[i][j]-1) * u0;
				}
			}


			// расчет сети
			for ( int i = 1; i <= 200; i ++ ){
				// пересчет значений для частей функции оптимизации
				for ( int ux = 1; ux <= N; ux ++ ){
					for ( int ui = 1; ui <= N; ui ++ ){
						double m1 = 0.0;
						double m2 = 0.0;
						double m3 = 0.0;
						double m4 = 0.0;
						// первая часть функции оптимизации (в каждой строке 1 город
						for ( int j = 1; j <= N; j ++ ){
							if ( j != ui ){
								m1 = m1 + V[ux][j];
							}
						}
						m1 = -A * m1;
						// вторая часть функции оптимизации (в каждом столбце 1 город
						for ( int y = 1; y <= N; y ++ ){
							if ( y != ux ){
								m2 = m2 + V[y][ui];
							}
						}
						m2 = -B * m2;
						// третья часть функции оптимизации (один маршрут)
						for ( int x = 1; x <= N; x ++ ){
							for ( int j = 1; j <= N; j ++ ){
								m3 = m3 + V[x][j];
							}
						}
						m3 = -C * ( m3 - N );
						// четвертая часть функции оптимизации (минимизация длины пути)
						for ( int y = 1; y <= N; y ++ ){
							if ( y != ux ){
								if ( ui == 1 ){
									m4 = m4 + d[ux][y] * ( V[y][ui + 1] + V[y][N] );
								}
								else if ( ui == N ){
									m4 = m4 + d[ux][y] * ( V[y][ui - 1] + V[y][1] );
								}
								else {
									m4  = m4 + d[ux][y] * ( V[y][ui + 1] + V[y][ui - 1] );
								}
							}
						}
						m4 = -D * m4;
						
						Udao[ux][ui] = -U[ux][ui] + m1 + m2 + m3 + m4;
						//System.out.println(m1 + " " + m2 + " " + m3 + " " + m4);

					}
				}

				

				// пересчет весов связей 
				for ( int str = 0; str < U.length; str ++ ){
					for ( int stb = 0; stb < U[str].length; stb ++ ){
						U[str][stb] = U[str][stb] + lamda * Udao[str][stb];
					}
				}
				
				// расчет выходных значений передаточной функции нейрона
				for ( int str = 0; str < V.length; str ++ ){
					for ( int stb = 0; stb < V[str].length; stb ++ ){
						V[str][stb] = ( 1 + tanh( U[str][stb]/u0 ) ) / 2;
					}
				}
				
				// пересчет еще раз выходов нейронов (повторное предъявление)
				for ( int ux = 1; ux <= N; ux ++ ){
					for ( int ui = 1; ui <= N; ui ++ ){
						if ( V[ux][ui] < 0.3 ){
							V[ux][ui] = 0;
						}
						if ( V[ux][ui] > 0.7 ){
							V[ux][ui] = 1;
						}
					}
				}
			}
			/*
			// вывод промежуточного решения
			System.out.println();
			for ( int str = 1; str < V.length; str ++ ){
				System.out.println();
				for ( int stb = 1; stb < V[str].length; stb ++ ){
					System.out.print(V[str][stb] + " ");
				}
			}
			*/
			// тестирование, проверка результатов
			// Подсчет общего числа едениц в матрице
			double test1 = 0;
			for ( int ux = 1; ux <= N; ux ++ ){
				for ( int ui = 1; ui <= N; ui ++ ){
					test1 = test1 + V[ux][ui];
				}
			}
			// проверка в каждой строке один город 
			double test2 = 0;
			for ( int x = 1; x <= N; x ++ ){
				for ( int i = 1; i <= N - 1; i ++ ){
					for ( int j = i + 1; j <= N; j ++ ){
						test2 = test2 + V[x][i] * V[x][j];
					}
				}
			}
			// проверка в каждом столбце один город
			double test3 = 0;
			for ( int i = 1; i <= N; i ++ ){
				for ( int x = 1; x <= N - 1; x ++ ){
					for ( int y = x + 1; y <= N; y ++ ){
						test3 = test3 + V[x][i] * V[y][i];
					}
				}
			}
			// проверка всех условий тестирования (проверки) матрицы
			if ( test1 == N && test2 == 0 && test3 == 0 ){
				toend = 1;
			}
			else{
				toend = 0;
			}
		}
		System.out.println();
		System.out.println("Calculation is completed for " + (System.currentTimeMillis() - start) / 1000 + " sec");
		// пересчет координат городов в порядке их следования
		double [] cityx_final = new double [cityx.length + 2];
		double [] cityy_final = new double [cityy.length + 2];
		for ( int j = 1; j <= N; j ++ ){
			for ( int i = 1; i <= N; i ++ ){
				if ( V[i][j] == 1 ){
					cityx_final[j] = cityx[i];
					cityy_final[j] = cityy[i];
				}
			}
		}
		cityx_final[N + 1] = cityx_final[1];
		cityy_final[N + 1] = cityy_final[1];
		// отображение городов и маршрута
		double td = 0;
		for ( int i = 1; i <= N - 1; i ++ ){
			td = td + Math.sqrt( Math.pow(( cityx_final[i] - cityx_final[i + 1] ) , 2) + Math.pow(( cityy_final[i] - cityy_final[i + 1] ) , 2) );
		}
		System.out.println();
		System.out.println("Length: " + td);

		//перевод координат городов в индексы городов
		int [] march = new int [cityx.length + 1];
		for ( int i = 0; i < cityx_final.length; i ++ ){
			for ( int j = 0; j < cityx.length; j ++ ){
				if ( cityx_final[i] == cityx[j] && cityy_final[i] == cityy[j] ){
					march[i] = j;
				}
			}
		}
		System.out.println();
		for ( int i = 1; i < march.length; i ++ ){
			System.out.print(march[i] + " ");
		}
		
	}
}